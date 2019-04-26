package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * user login
     * @param userName
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value="login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String userName, String password, HttpSession session){
//        service --> mybatis -->dao
        ServerResponse response = iUserService.login(userName,password);
        if (response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "logout.do",method = RequestMethod.GET)
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @ResponseBody
    @RequestMapping(value = "register.do",method = RequestMethod.GET)
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    @ResponseBody
    @RequestMapping(value = "check_valid.do",method = RequestMethod.GET)
    public ServerResponse<String> checkValid(String string,String type){
        return iUserService.checkValid(string,type);
    }

    @ResponseBody
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.GET)
    public ServerResponse<User> getUerInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("user does not login, can not get user message");
    }

    @ResponseBody
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.GET)
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    @ResponseBody
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.GET)
    //use local cache
    public ServerResponse<String> forCheckAnswer(String username,String password,String answer){
        return iUserService.checkAnswer(username,password,answer);
    }

    @ResponseBody
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.GET)
    public ServerResponse<String> forgetResetPassword(String userName,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(userName,passwordNew,forgetToken);
    }

    @ResponseBody
    @RequestMapping(value = "reset_password.do",method = RequestMethod.GET)
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("user doesn't login");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    @ResponseBody
    @RequestMapping(value = "update_information.do",method = RequestMethod.GET)
    public ServerResponse<User> updateInformation(HttpSession session, User user){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ServerResponse.createByErrorMessage("currentUser doesn't login");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "get_information.do",method = RequestMethod.GET)
    public ServerResponse<User> getInformation(HttpSession session){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ServerResponse.createByErrorCodeMessage(
                    ResponseCode.NEED_LOGIN.getCode(),
                    "currentUser doesn't login,need to force login,status=10");
        }
        return iUserService.getInformation(currentUser.getId());
    }
}
