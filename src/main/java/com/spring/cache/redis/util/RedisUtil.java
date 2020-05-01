package com.spring.cache.redis.util;

import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

@Component
public class RedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    @Resource
    RedisTemplate<Serializable,Object> redisTemplate;

    /**
     * 写入缓存
     */
    public boolean set(String key,Object object){
        boolean res = false;
        try{
            redisTemplate.opsForValue().set(key,object);
            res = true;
        }catch (Exception e){
            logger.error("redis set error:{}",e.getMessage(),e);
        }
        return res;
    }

    /**
     * 读取缓存
     */
    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 发布消息
     */
    public void publish(){
        redisTemplate.convertAndSend("*","redis publish success");
    }

    /**
     * 批量删除
     */
    public void removePattern(String pattern){
        Set<Serializable> keys = new HashSet<>();
        //获取所有符合条件的key
        this.scan(pattern,item -> {
            //符合条件的key
            Serializable key = new String(item, StandardCharsets.UTF_8);
            keys.add(key);
        });
        logger.info("待清理key的数量:{}",keys.size());
        LocalDateTime x1 = LocalDateTime.now();
        if(!CollectionUtils.isEmpty(keys)){
            StreamSupport.stream(Iterables.partition(keys,1000).spliterator(),false)
                .forEach(list -> {
                    //批次清理key
                    redisTemplate.delete(list);
                });
        }
        logger.info("清理完毕，耗时(ms):{}", Duration.between(x1,LocalDateTime.now()).getNano()/1000000);
    }

    /**
     * 获取所有符合条件的key
     */
    private void scan(String pattern, Consumer<byte[]> consumer){
        redisTemplate.execute((RedisConnection connection) ->{
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(pattern).count(Long.MAX_VALUE).build())) {
                cursor.forEachRemaining(consumer);
            }catch (Exception e){
                logger.error("redis scan error:{}",e.getMessage(),e);
            }
            return null;
        });
    }
}
