package com.spring.cache.service.impl;

import com.spring.cache.entity.User;
import com.spring.cache.mapper.UserMapper;
import com.spring.cache.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 可以用@CachePut更新，而@Cacheable在取值的时候，是@CachePut更新后的值。
 */
@Service
public class UserServiceImpl implements UserService {

    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserMapper userMapper;

    @Override
    public User findUserById(Integer id) {
        logger.info("{}查询走的是数据库",id);
        return userMapper.findUserById(id);
    }

    @Override
    public List<User> query() {
        return userMapper.query();
    }

    @Override
    public User updateUser(User user) {
        userMapper.updateUser(user);
        return user;
    }

    @Override
    public int create(User user) {
        return userMapper.create(user);
    }

    @Override
    public int deleteUserById(Integer id) {
        return userMapper.deleteUserById(id);
    }
}
