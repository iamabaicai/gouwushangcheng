package com.pinyougou.sellergoods;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;

import java.util.List;
import java.util.Map;

public interface BrandService {

    public List<TbBrand> findAllBrands() throws Exception;

    public PageResult<TbBrand> findByPage(Integer currentPage, Integer pageSize) throws Exception;


    public PageResult<TbBrand> findByPage(TbBrand tbBrand, Integer currentPage, Integer pageSize) throws Exception;


    //新建一个品牌
    public void addBrand(TbBrand tbBrand) throws Exception;


    public TbBrand findBrandById(Long id) throws Exception;


    public void updateBrand(TbBrand tbBrand) throws Exception;


    //批量删除
    public void deleteBrands(long[] ids) throws Exception;


    public boolean findBrandByName(TbBrandExample tbBrand) throws Exception;


    //自定义的查询方法
    List<Map> selectBrandList();

}
