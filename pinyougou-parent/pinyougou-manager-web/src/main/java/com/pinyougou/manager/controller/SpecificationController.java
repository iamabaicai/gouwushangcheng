package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import com.pinyougou.sellergoods.SpecificationService;
import entity.Specification;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSpecification;


import entity.PageResult;
import entity.Result;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbSpecification> findAll() {
        return specificationService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return specificationService.findPage(page, rows);
    }

    /**
     * 增加规格及其关联的规格选项列表
     *
     * @param specification
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Specification specification) {
        if (specification != null) {
            if (specification.getSpecification().getSpecName() != null &&
                    specification.getSpecificationOptionList() != null) {
                try {
                    specificationService.add(specification);
                    return new Result(true, "增加成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Result(false, "增加失败");
                }
            }
            return new Result(false, "增加失败");
        }
        return new Result(false, "增加失败");
    }


    /**
     * 修改(主表修改，关联子表相关外键的记录删除然后重新插入)
     *
     * @param specification
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Specification specification) {
        try {
            specificationService.update(specification);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体(修改的时候需要数据回显)
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Specification findOne(Long id) {
        return specificationService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            specificationService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param specification
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbSpecification specification, int page, int rows) {
        return specificationService.findPage(specification, page, rows);
    }

    /**
     * 自定义的查询方法(查询关联规格)
     *
     * @return
     */
    @RequestMapping("/selectSpecificationList")
    public List<Map> selectSpecificationList() {

        return specificationService.selectSpecificationList();
    }

}
