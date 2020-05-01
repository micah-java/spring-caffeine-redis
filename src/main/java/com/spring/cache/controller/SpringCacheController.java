package com.spring.cache.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.spring.cache.entity.User;
import com.spring.cache.redis.util.RedisUtil;
import com.spring.cache.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class SpringCacheController {

    private static Logger logger = LoggerFactory.getLogger(SpringCacheController.class);

    @Autowired
    UserService userService;

    @Resource
    Cache<Object, Object> caffeineCache;

    @Autowired
    RedisUtil redisUtil;

    @GetMapping("/query")
    public List<User> query(){
        List<User> list = userService.query();
        // 加入缓存
        list.forEach( e -> {
            caffeineCache.put(e.getId(),e);
            redisUtil.set(String.valueOf(e.getId()),e);
        });
        return list;
    }

    @GetMapping("/create")
    public Map saveUser(@RequestParam String name, @RequestParam Integer age){
        Map map = new HashMap();
        int lines = userService.create(new User(name,age));
        map.put("lines",lines);
        return map;
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable Integer id){
        User user = userService.findUserById(id);
        return user;
    }

    /**
     * 不用注解操作缓存
     */
    @GetMapping("/get/{id}")
    public User getUserById(@PathVariable Integer id){
        // 先从一级缓存读取
        User u = (User)caffeineCache.getIfPresent(id);
        if(Objects.nonNull(u)){
            logger.info("从caffeine缓存中获取数据:{}",u);
            return u;
        }
        //再从二级缓存中读取
        User o = (User)redisUtil.get(String.valueOf(id));
        if(Objects.nonNull(o)){
            logger.info("从redis缓存中获取数据:{}",o);
            //更新一级缓存
            caffeineCache.put(id,o);
            return o;
        }
        User user = userService.findUserById(id);
        logger.info("从数据库中获取数据:{}",user);
        return user;
    }

    @GetMapping("/update")
    public Map updateUser(@RequestParam Integer id, @RequestParam String name, @RequestParam Integer age){
        Map map = new HashMap();
        User user = userService.updateUser(new User(id,name,age));
        map.put("user",user);
        return map;
    }

    @GetMapping("/del/{id}")
    public Map deleteUser(@PathVariable Integer id){
        Map map = new HashMap();
        //int lines = userService.deleteUserById(id);
        //删除本地缓存
        caffeineCache.invalidate(id);
        map.put("result",true);
        return map;
    }

    /**
     * redis发布消息
     */
    @GetMapping("/publish")
    public Map publish(){
        Map map = new HashMap();
        redisUtil.publish();
        map.put("result",true);
        return map;
    }
}
