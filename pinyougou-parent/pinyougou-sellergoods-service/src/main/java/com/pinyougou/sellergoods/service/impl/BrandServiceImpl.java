package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service //阿里巴巴的server注解
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper tbBrandMapper;
    @Override
    public List<TbBrand> findAllBrands() throws Exception {
        return tbBrandMapper.selectByExample(null);
    }

    @Override
    public PageResult<TbBrand> findByPage(Integer currentPage, Integer pageSize) throws Exception {
        PageHelper.startPage(currentPage,pageSize);//currentPage当前页, pageSize每页条数

        //当前页数据
        List<TbBrand> tbBrands = tbBrandMapper.selectByExample(null);
        //import com.github.pagehelper.PageInfo;封装
        PageInfo<TbBrand> pageInfo=new PageInfo<>(tbBrands);

        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 条件分页查询
     * @param tbBrand
     * @param currentPage
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Override
    public PageResult<TbBrand> findByPage(TbBrand tbBrand, Integer currentPage, Integer pageSize) throws Exception {
        PageHelper.startPage(currentPage,pageSize);//currentPage当前页, pageSize每页条数
        //构造分页查询
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        if (tbBrand!=null){
            if (tbBrand.getName()!=null&&!"".equals(tbBrand.getName())){
                //模糊查询
                criteria.andNameLike("%" + tbBrand.getName() + "%");
            }
            if (tbBrand.getFirstChar()!=null&&!"".equals(tbBrand.getFirstChar())){

                criteria.andFirstCharEqualTo(tbBrand.getFirstChar().toUpperCase());
            }
        }
        //当前页数据
        List<TbBrand> tbBrands = tbBrandMapper.selectByExample(example);
        //import com.github.pagehelper.PageInfo;封装
        PageInfo<TbBrand> pageInfo=new PageInfo<>(tbBrands);

        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    //新建一个品牌
    @Override
    public void addBrand(TbBrand tbBrand) throws Exception {
        tbBrandMapper.insert(tbBrand);

    }

    @Override
    public TbBrand findBrandById(Long id) throws Exception {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateBrand(TbBrand tbBrand) throws Exception {
    tbBrandMapper.updateByPrimaryKey(tbBrand);
    }

    //批量删除
    @Override
    public void deleteBrands(long[] ids) throws Exception {
        for (long id : ids) {
            tbBrandMapper.deleteByPrimaryKey(id);
        }

    }

    @Override
    public boolean findBrandByName(TbBrandExample tbBrand) throws Exception {

        List<TbBrand> tbBrands = tbBrandMapper.selectByExample(tbBrand);
        if (tbBrands!=null){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public List<Map> selectBrandList() {
        return tbBrandMapper.selectBrandList();
    }
}
