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
}
