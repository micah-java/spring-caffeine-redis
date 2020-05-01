package com.spring.cache.mapper;

import com.spring.cache.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM User WHERE id = #{id}")
    User findUserById(Integer id);

    @Select("SELECT * FROM User")
    List<User> query();

    @Update("update user set name=#{name},age=#{age} where id=#{id}")
    int updateUser(User user);

    @Insert("insert into user set name=#{name},age=#{age}")
    int create(User user);

    @Delete("DELETE FROM USER WHERE id=#{id}")
    int deleteUserById(Integer id);
}
