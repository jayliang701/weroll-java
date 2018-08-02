package com.magicfish.weroll.service;

public interface UserService {

    String login(String username, String password) throws Exception;

//    PageInfo<User> findAllUser(int pageNum, int pageSize);
}
