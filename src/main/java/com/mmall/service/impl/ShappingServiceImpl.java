package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Service("iShippingService")
public class ShappingServiceImpl implements IShippingService {

    @Autowired
    ShippingMapper shippingMapper;

    //因为前端输入的时候肯定不会输入userId,所以尽管是shipping里有这个字段，但是是空，所以需要从session里拿过来
    public ServerResponse add(Integer userId,Shipping shipping){
        System.out.println(shipping.getUserId());
        shipping.setUserId(userId);
        //和前端约定把id传给前端 这里改了insert的SQL ，加上了两个配置 useGeneratedKeys="true" keyProperty="id"
        //这样id 就会填充到shipping的 id 上
        int rowCount = shippingMapper.insert(shipping);
        System.out.println(shipping.getReceiverName());
        if (rowCount > 0) {
            Map resultMap = Maps.newHashMap();
            resultMap.put("shippingId", shipping.getId());
            return ServerResponse.createBySuccess("create shipping success", resultMap);
        }
        return ServerResponse.createByErrorMessage("create shipping failed");
    }

    public ServerResponse<Shipping> delete(Integer userId,Integer shippingId){
        int resultCount = shippingMapper.deleteByShippingIdUserId(shippingId,userId);
        if (resultCount > 0)
            return ServerResponse.createBySuccessMessage("delete shipping success");
        return ServerResponse.createByErrorMessage("delete shipping failed");
    }

    public ServerResponse update(Integer userId,Shipping shipping){
        shipping.setUserId(userId);
//        another way
//        if (shippingMapper.selectByPrimaryKey(shipping.getId()).getUserId() == userId)
        int rowCount = shippingMapper.updateByShipping(shipping);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("update shipping success");
        } else
            return ServerResponse.createByErrorMessage("update shipping failed");
    }

    public ServerResponse<Shipping> select(Integer userId,Integer shippingId){
        Shipping resultShipping = shippingMapper.selectByShipingIdUserId(shippingId,userId);
        if (resultShipping != null)
            return ServerResponse.createBySuccess("find shipping success",resultShipping);
        return ServerResponse.createByErrorMessage("cannot find this shipping");
    }

    public ServerResponse<PageInfo> list(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);

        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }


}
