package com.spring.cache.service;


import com.spring.cache.entity.User;

import java.util.List;

public interface UserService {

    public User findUserById(Integer id);
    public List<User> query();
    public User updateUser(User user);
    public int create(User user);
    public int deleteUserById(Integer id);
}
