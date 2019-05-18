package com.mmall.dao;

import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    /////////////////////////////

    List<Product> selectList();

    List<Product> selectByNameAndId(@Param("productName") String productName, @Param("productId") Integer productId);

    List<Product> selectByNameAndCategoryIds(@Param("produceName") String produceName, @Param("categoryIdList") List<Integer> categoryIdList);


}