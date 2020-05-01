package com.spring.cache.redis;

import com.github.benmanes.caffeine.cache.Cache;
import com.spring.cache.entity.User;
import com.spring.cache.redis.util.RedisUtil;
import com.spring.cache.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * redis订阅消息
 */
@Component
public class RedisReceiver implements MessageListener {
    private static Logger logger = LoggerFactory.getLogger(RedisReceiver.class);

    @Autowired
    UserService userService;

    @Resource
    Cache<Object, Object> caffeineCache;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void onMessage(Message message, byte[] bytes) {
        logger.info("redis receive message:{}",message.toString());
        List<User> list = userService.query();
        // 加入缓存
        list.forEach( e -> {
            caffeineCache.put(e.getId(),e);
            redisUtil.set(String.valueOf(e.getId()),e);
        });
    }
}
