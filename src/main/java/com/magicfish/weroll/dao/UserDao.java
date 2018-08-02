package com.magicfish.weroll.dao;

import com.magicfish.weroll.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

public interface UserDao {

    @Select("select * from  user where username = #{username}")
    User findByUsername(String username);

//    List<User> selectUsers();
}
