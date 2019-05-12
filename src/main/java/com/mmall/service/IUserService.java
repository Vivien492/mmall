package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

// I for interface
public interface IUserService {
    //public is redundant for a interface
    ServerResponse<User> login(String userName, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String string,String type);

    ServerResponse selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username,String questiion,String answer);

    ServerResponse<String> forgetResetPassword(String userName,String passwordNew,String forgetToken);

    ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer userId);

    ServerResponse checkAdminRole(User user);
}
