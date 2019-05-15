package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;


    public ServerResponse saveOrUpdateProduct(Product product){
        if (product != null){
            if (StringUtils.isNotBlank(product.getSubImages())){
                String[] subImageArray = product.getSubImages().split(",");//和前端约定逗号分隔
                if (subImageArray.length > 0){
                    //the user upload lots of images,we set the first one main image
                    product.setMainImage(subImageArray[0]);
                }
            }
            //the interface is set to give a id parameter when update.
            if (product.getId() != null){
                int rowCount = productMapper.updateByPrimaryKey(product);
                if (rowCount >0)
                    return ServerResponse.createBySuccess("update product success");
                else
                    return ServerResponse.createByErrorMessage("update product failed");
            } else {
                int rowCount = productMapper.insert(product);
                if (rowCount > 0)
                    return ServerResponse.createBySuccessMessage("add product success");
                else
                    return ServerResponse.createByErrorMessage("add product failed");
            }
        }
        return ServerResponse.createByErrorMessage("wrong parameter to add or update product");
    }

    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
        if (productId == null || status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode()
                    ,ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId( productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0){
            return ServerResponse.createBySuccessMessage("set status success");
        }
        return ServerResponse.createByErrorMessage("set status failed");
    }

    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if (productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode()
                    ,ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMessage("product is deleted or off sale");
        }
        //vo object---value object
        // more complicated --> pojo--vo--bo , view object ,business object
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);

    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtittle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImage(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        //imageHost
        productDetailVo.setImageHost(PropertiesUtil.getProperty(
                "ftp.server.http.prefix","http://img.happymmall.com/"));

        //parent category id
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null)
            productDetailVo.setParentCategoryId(0);//default is root node
        else
            productDetailVo.setParentCategoryId(category.getParentId());

        //create time
        // we get a ms number through mybatis. so we need format.
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        //update time
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize){
        // page helper
        //1 start page
        PageHelper.startPage(pageNum,pageSize);
        //2 sql
        // whithout ; when use pagehelper,cause it will add something like "limit 10 offset 1" to the sql, ;will make it wrong
        List<Product> productList = productMapper.selectList();

        return getPageInfoServerResponse(productList);
    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setName(product.getName());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        productListVo.setSubtitle(product.getSubtitle());

        productListVo.setImageHost(PropertiesUtil.getProperty(
                "ftp.server.http.prefix","http://img.happymmall.com/"));
        return productListVo;
    }

    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList =  productMapper.selectByNameAndId(productName,productId);

        //here uses intellij "extract method" !!
        return getPageInfoServerResponse(productList);

    }

    // this is the method extracted by intellij
    private ServerResponse<PageInfo> getPageInfoServerResponse(List<Product> productList) {
        List<ProductListVo> productListVos = new ArrayList<>();
        for (Product p :
                productList) {
            ProductListVo plv = assembleProductListVo(p);
            productListVos.add(plv);
        }

        PageInfo result = new PageInfo(productList);
        result.setList(productListVos);
        return ServerResponse.createBySuccess(result);
    }


    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if (productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode()
                    ,ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMessage("product is deleted or off sale");
        }
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("product is deleted or off sale");
        }
        //vo object---value object
        // more complicated --> pojo--vo--bo , view object ,business object
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    public ServerResponse<PageInfo> getProductByKeyWordCategory(String keyword, Integer categoryId,
                                                                int pageNum, int pageSize, String orderBy) {
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIds = Lists.newArrayList();
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVos = Lists.newArrayList();
                PageInfo result = new PageInfo(productListVos);
                return ServerResponse.createBySuccess(result);
            }
            categoryIds = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if (StringUtils.isNotBlank(keyword)) {
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum, pageSize);

        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword) ? null : keyword,
                categoryIds.size() == 0 ? null : categoryIds);

        List<ProductListVo> productListVos = Lists.newArrayList();
        for (Product product :
                productList) {
            ProductListVo plv = assembleProductListVo(product);
            productListVos.add(plv);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVos);
        return ServerResponse.createBySuccess(pageInfo);
    }


}
