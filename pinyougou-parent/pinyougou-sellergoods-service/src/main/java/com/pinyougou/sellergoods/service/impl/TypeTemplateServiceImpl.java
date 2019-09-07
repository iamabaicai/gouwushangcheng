package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.sellergoods.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;


import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;



    /**
     * 查询全部
     */
    @Override
    public List<TbTypeTemplate> findAll() {
        return typeTemplateMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);

        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
       
        return pageResult;
    }




    /**
     * 增加
     */
    @Override
    public void add(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKey(typeTemplate);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbTypeTemplateExample example = new TbTypeTemplateExample();
        Criteria criteria = example.createCriteria();

        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
                criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
            }
            if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
                criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
            }
            if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
                criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
            }

        }
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);

        saveToRedis();//存入数据到缓存
        return new PageResult(page.getTotal(), page.getResult());
    }


    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 将数据存入缓存
     */
    private void saveToRedis(){
        //获取模板数据
        List<TbTypeTemplate> typeTemplateList = findAll();
        //循环模板
        for(TbTypeTemplate typeTemplate :typeTemplateList){
            //存储品牌列表
            List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandList);
            //存储规格列表
            //根据模板ID查询规格列表
            List<Map> specList = findSpecList(typeTemplate.getId());
            redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specList);
        }
    }


    /**
     * 只要typeTemplate表的id和name(text)两列作为下拉框备选项
     *
     * @return
     */
    //只要typeTemplate表的id和name(text)两列
    public List<Map> selectTypeTemplateList() {
        return typeTemplateMapper.selectTypeTemplateList();
    }

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Override
    public List<Map> findSpecList(Long templateId) {
        //查询模板对象
        TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(templateId);
        //将模板对象的规格[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]转为list
        String specIdsJson = typeTemplate.getSpecIds();//字符串
        List<Map> list = JSON.parseArray(specIdsJson, Map.class);
        for (Map map : list) {
            Integer id = (Integer) map.get("id");

            TbSpecificationOptionExample example=new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(new Long(id));
            List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);
            map.put("options",options);
            //[{"id":27,"text":"网络","options":[{},{}]},{"id":27,"text":"网络","options":[{},{}]}]
        }
        return list;
    }
}
