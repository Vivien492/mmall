package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

public class OrderProductVo {
    private List<OrderItemVo> orderItemVoList;
    private BigDecimal productTotoalPrice;
    private String imageHost;

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public BigDecimal getProductTotoalPrice() {
        return productTotoalPrice;
    }

    public void setProductTotoalPrice(BigDecimal productTotoalPrice) {
        this.productTotoalPrice = productTotoalPrice;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
