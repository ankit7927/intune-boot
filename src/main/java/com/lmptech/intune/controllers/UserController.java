package com.lmptech.intune.controllers;

import com.lmptech.intune.models.UserModel;
import com.lmptech.intune.models.ErrorMessage;
import com.lmptech.intune.services.UserService;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("profiles")
    public ResponseEntity<?> getUserProfile(@RequestBody List<String> ids) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserModel> userProfile = userService.getUserProfile(user.getUsername(), ids);
        return new ResponseEntity<>(userProfile, HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<?> addUser(@RequestBody UserModel userModel) {
        try {
            userService.createUser(userModel);
            Map<String, String> res = new HashMap<>();
            res.put("message", "created");
            return new ResponseEntity<>(res, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorMessage("email or username already used"), HttpStatus.BAD_REQUEST);
        }
    }
}
