package com.mmall.dao;

import com.mmall.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    /////////////////////////////////////////

    int selectByParentId(int parentId);

    List<Category> selectCategoryChildrenByParentId(int parentId);
}