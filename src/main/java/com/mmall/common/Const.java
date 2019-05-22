package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {
    public static final String CURRENT_USER = "currentUser";

    public interface Role{
        int ROLE_CUSTMER = 0; // nomal user
        int ROLE_ADMIN = 1; // administrator
    }

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public enum ProductStatusEnum{
        ON_SALE(1,"on sale");
        private String value;
        private int code;

        ProductStatusEnum(int code,String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    public interface ProductListOrderBy {
        // Set's contains() O(1)
        //List .............O(n)
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }

    public interface cart{
        int CHECKIED = 1; //the product is checked in the cart
        int UN_CHECKED = 0;

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUC = "LIMIT_NUM_SUC";
    }

    public enum OrderStatusEnum{
        CANCELED(0,"canceled"),
        NO_PAY(10,"not pay"),
        PAID(20,"paid"),
        SHIPPED(40,"shipped"),
        ORDER_SUCCESS(40,"order finished"),
        ORDER_CLOSED(60,"order closed")
        ;

        private String value;
        private int code;

        OrderStatusEnum(int code,String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }
        public int getCode() {
            return code;
        }

    }

    public interface AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_CLOSED = "TRADE_CLOSED";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public enum PayPlatformEnum{
        ALIPAY(1,"zhi fu bao");

        ;

        private String value;
        private int code;

        PayPlatformEnum(int code,String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }
        public int getCode() {
            return code;
        }
    }

}
