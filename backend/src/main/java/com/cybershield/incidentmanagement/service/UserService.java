package com.cybershield.incidentmanagement.service;

import com.cybershield.incidentmanagement.entity.User;
import com.cybershield.incidentmanagement.repository.UserRepository;

public class UserService {

    private final UserRepository repository =
            new UserRepository();

    public boolean register(User user){

        if(user.getName().isBlank()) return false;
        if(user.getEmail().isBlank()) return false;
        if(user.getPassword().length() < 6) return false;

        return repository.save(user);
    }
}