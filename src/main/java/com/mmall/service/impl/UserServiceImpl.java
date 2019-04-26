package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//Impl for implement
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    @Autowired
    public ServerResponse<User> login(String userName, String password) {
        int resultCount = userMapper.checkUserName(userName);
        if (0 == resultCount){
            return ServerResponse.createByErrorMessage("user name do not exist");
        }

        //TODO login with password MD5
        String MD5Password  = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(userName,MD5Password);

        if (user == null){
            return  ServerResponse.createByErrorMessage("wrong password");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("login success",user);
    }

    public ServerResponse<String> register(User user){
        ServerResponse vaildResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if (!vaildResponse.isSuccess()){
            return vaildResponse;
        }
        vaildResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if (!vaildResponse.isSuccess()){
            return vaildResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTMER);
        //MD5 encryption
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (0 == resultCount){
            return  ServerResponse.createByErrorMessage("register failed");
        }
        return ServerResponse.createBySuccessMessage("register succes s");
    }

    public ServerResponse<String> checkValid(String string,String type){
        if (StringUtils.isNoneBlank(type)){
            if (Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUserName(string);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMessage("uer name already exist");
                }
            }else if (Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(string);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMessage("Email already exist");
                }
            }
        } else return ServerResponse.createByErrorMessage("error parameter");

        return ServerResponse.createBySuccessMessage("verify success");
    }
}
