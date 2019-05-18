package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

//Impl for implement
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String userName, String password) {
        int resultCount = userMapper.checkUserName(userName);
        if (0 == resultCount){
            return ServerResponse.createByErrorMessage("user name do not exist");
        }

        // login with password MD5
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
        return ServerResponse.createBySuccessMessage("register success");
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

    public ServerResponse selectQuestion(String username){
        ServerResponse<String> valid = this.checkValid(username,Const.USERNAME);
        if (valid.isSuccess()){
            return ServerResponse.createByErrorMessage("user does not exist");
        }
        String question = userMapper.selectQuestionByUserName(username);
        if (StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("the question for finding back password is blank");
    }


    public ServerResponse<String> checkAnswer(String username,String questiion,String answer) {
        //here we use three parameters, let db do the check stuff, we don't need check the answer and then compare.
        int resultCount = userMapper.checkAnswer(username,questiion,answer);
        if (resultCount > 0){
            // the question and answer belong to this user, and it's right
            String forgetToken = UUID.randomUUID().toString();
            //add "token_" as a namespace,or just to say we use it to distinguish.
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("wrong answer");
    }

    public ServerResponse<String> forgetResetPassword(String userName,String passwordNew,String forgetToken){
        if (StringUtils.isBlank(forgetToken)){
            ServerResponse.createByErrorMessage("parameter error,token is needed");
        }
        // need to varify username, because we use "token_"+username as key ,is username is null, it is not safe
        //everyone can get the token use a blank username
        ServerResponse<String> valid = this.checkValid(userName,Const.USERNAME);
        if (valid.isSuccess()){
            return ServerResponse.createByErrorMessage("user does not exist");
        }

        //get token from cache
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+userName);
        if (StringUtils.isBlank(token)){//not hit the target
            ServerResponse.createByErrorMessage("token is not valid or past due");
        }
        if (StringUtils.equals(forgetToken,token)){
            String MD5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(userName,MD5Password);
            if (rowCount > 0){
                return ServerResponse.createBySuccessMessage("update password success");
            }
        }else {
            return ServerResponse.createByErrorMessage("token mistake,please get token again");
        }
        return ServerResponse.createByErrorMessage("update password failed");
    }

    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        //in case of 横向越权,so need to varify user and his old password
        //cause we will select a count(1),if we do not use the specific user,count>0 will be true
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if (0 == resultCount){
            return ServerResponse.createByErrorMessage("wrong old password");
        }
        //why?!!!! password in user is old? new ? when do they become new?
        //done with this question by set user's password to passwordNew
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0){
            return ServerResponse.createBySuccessMessage("update password success");
        }
        return ServerResponse.createByErrorMessage("update password failed");
    }

    public ServerResponse<User> updateInformation(User user){
        //user name cannot be update
        //email should be verified, already exist?
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if (resultCount > 0){
            return ServerResponse.createByErrorMessage("email already exist, change it and try again");
        }
        User updateUser = user;
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount >0){
            return ServerResponse.createBySuccess("update success",updateUser);
        }
        return ServerResponse.createByErrorMessage("update failed");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null){
            ServerResponse.createByErrorMessage("can not find current user");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }


    /**
     * check if user is admin or not
     * @param user
     * @return
     */
    //backend
    public ServerResponse checkAdminRole(User user){
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN)
            return ServerResponse.createBySuccess();
        return ServerResponse.createByError();
    }

}
