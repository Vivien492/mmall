package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse addCategory(String categoryName,Integer parentId){
        //!! check parameter
        if (parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("wrong parameter to add category");
        }
        //check if paretId exists  ?? do not check(in the vedio)????
        if (categoryMapper.selectByParentId(parentId) >0 || parentId == 0){
            // add
            Category category = new Category();
            category.setName(categoryName);
            category.setParentId(parentId);
            category.setStatus(true);

            int row = categoryMapper.insert(category);
            if (row > 0)
                return ServerResponse.createBySuccessMessage("add category successfully");
        }
        return ServerResponse.createByErrorMessage("add category failed");
    }

    public ServerResponse updateCategoryName(Integer catgegoryId,String categoryName){
        //!! check parameter
        if (catgegoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("wrong parameter to update category");
        }
        Category category = new Category();
        category.setId(catgegoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category); // only update the not null column
        if (rowCount > 0)
            return ServerResponse.createBySuccessMessage("update category name success");
        return ServerResponse.createByErrorMessage("update category name failed");
    }

    public ServerResponse<List<Category>>  getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            logger.info("children category not found");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * find the id and its childrens id
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet(); // guava
        findChildCategory(categorySet,categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId != null){
            for (Category c :
                    categorySet) {
                categoryIdList.add(c.getId());
            }
        }
        logger.info("categoryList",categoryIdList.get(0));
        return ServerResponse.createBySuccess(categoryIdList);
    }

    //remember overwrite hashCode() and equals() of Category
    // Set will ensure that there is no duplicated elements.
    private Set<Category> findChildCategory(Set<Category> categorySet ,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null){
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category c :
                categoryList) {
            findChildCategory(categorySet, c.getId());
        }
        return categorySet;
    }

    //this one can only find the parallel children.
    private Set<Category> findChildCategory(Integer categoryId){
        List<Category> categories = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        Set<Category> result = new HashSet<>(categories);
//        we use mybatis so here categories will not be null,in other time we should check it,
//        or else foreach will throw a nullPointerException.
        for (Category c :
                categories) {
            List<Category> list = categoryMapper.selectCategoryChildrenByParentId(c.getId());
            result.addAll(list);
        }
        if (CollectionUtils.isEmpty(result)){
            logger.info("not found");
        }
        return result;
    }

}
