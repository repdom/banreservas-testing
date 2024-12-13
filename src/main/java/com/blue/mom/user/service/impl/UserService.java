package com.blue.mom.user.service.impl;

import com.blue.mom.user.entity.User;
import com.blue.mom.user.exeption.UserNotFoundException;

import java.util.List;

public interface UserService {
    User getUserById(long id) throws UserNotFoundException;

    List<User> getAllUsers();

    User updateUser(long id, User user) throws UserNotFoundException;

    User saveUser(User user);

    void deleteUser(long id) throws UserNotFoundException;

    User getUserByUsername(String username);
}
