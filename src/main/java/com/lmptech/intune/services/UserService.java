package com.lmptech.intune.services;

import com.lmptech.intune.data.models.UserModel;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<UserModel> getAllUser() {
        return mongoTemplate.findAll(UserModel.class, "Users");
    }

    public List<UserModel> getUserProfile(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.find(query, UserModel.class);
    }

    public void createUser(UserModel userModel) {
        userModel.setVerified(false);
        mongoTemplate.insert(userModel, "Users");
    }

    public UpdateResult updateUser(UserModel userModel) {
        Query query = Query.query(Criteria.where("_id").is(userModel.getId()));

        Update update = new Update();
                update.set("name", userModel.getName());
                update.set("username", userModel.getUsername());

        return mongoTemplate.updateFirst(query, update, UserModel.class);
    }
}
