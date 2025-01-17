package com.lmptech.intune.configs;

import com.lmptech.intune.services.UserDetailsServiceImpl;
import com.lmptech.intune.utility.JwtUtility;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@NoArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !(header.startsWith("Bearer "))) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);
            String userId = jwtUtility.getUserId(token);

            SecurityContext securityContext = SecurityContextHolder.getContext();

            if(userId != null && securityContext.getAuthentication() == null){
                UserDetails details = userDetailsService.loadUserById(userId);
                if(jwtUtility.validateToken(token, details)){
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException expiredJwtException) {
            response.sendError(HttpStatus.CONFLICT.value(), "token expired");
        } catch (MalformedJwtException malformedJwtException) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "malformed token");
        } catch (AccessDeniedException accessDeniedException) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Access denied");
        }
    }
}
