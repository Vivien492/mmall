package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

// I for interface
public interface IUserService {
    ServerResponse<User> login(String userName, String password);

    ServerResponse<String> register(User user);

    public ServerResponse<String> checkValid(String string,String type);
}
