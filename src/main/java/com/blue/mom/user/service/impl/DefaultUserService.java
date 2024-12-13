package com.blue.mom.user.service.impl;

import com.blue.mom.user.entity.User;
import com.blue.mom.user.exeption.UserNotFoundException;
import com.blue.mom.user.repository.UserRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class DefaultUserService implements UserService {
    private final UserRepository userRepository;

    @Inject
    public DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserById(long id) throws UserNotFoundException {
        return userRepository.findByIdOptional(id).orElseThrow(() -> new UserNotFoundException("There user doesn't exist"));
    }

    public User getUserByUsername(String username) {
        return userRepository.find("username", username).firstResult();
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.listAll();
    }

    @Transactional
    @Override
    public User updateUser(long id, User user) throws UserNotFoundException {
        User existingUser = getUserById(id);
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setAge(user.getAge());
        userRepository.persist(existingUser);
        return existingUser;
    }

    @Transactional
    @Override
    public User saveUser(User user) {
        userRepository.persistAndFlush(user);
        return user;
    }

    @Transactional
    @Override
    public void deleteUser(long id) throws UserNotFoundException {
        userRepository.delete(getUserById(id));
    }
}
