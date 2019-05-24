package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;


    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpSession session,
                                              @RequestParam(value = "pageNo",defaultValue = "1") int pageNo,
                                              @RequestParam(value = "pagesize",defaultValue = "10") int pagesize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"please login");

        if (iUserService.checkAdminRole(user).isSuccess()){
            //update category name
            return iOrderService.manageList(pageNo,pagesize);
        }else {
            return ServerResponse.createByErrorMessage("not a admin,do not have authority");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> orderDetail(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"please login");

        if (iUserService.checkAdminRole(user).isSuccess()){
            //update category name
            return iOrderService.manageDetail(orderNo);
        }else {
            return ServerResponse.createByErrorMessage("not a admin,do not have authority");
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderSearch(HttpSession session, Long orderNo,
                                               @RequestParam(value = "pageNo",defaultValue = "1") int pageNo,
                                               @RequestParam(value = "pagesize",defaultValue = "10") int pagesize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"please login");

        if (iUserService.checkAdminRole(user).isSuccess()){
            //update category name
            return iOrderService.manageSearch(orderNo,pageNo,pagesize);
        }else {
            return ServerResponse.createByErrorMessage("not a admin,do not have authority");
        }
    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> orderSendGoods(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"please login");

        if (iUserService.checkAdminRole(user).isSuccess()){
            //update category name
            return iOrderService.manageSendGoods(orderNo);
        }else {
            return ServerResponse.createByErrorMessage("not a admin,do not have authority");
        }
    }

}
