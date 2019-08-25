package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.PageResult;

import com.pinyougou.entity.ResultInfo;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController //@Controller + 每个方法加@ResponseBody
@RequestMapping("/brand")
public class BrandController {
    @Reference//远程注入使用阿里巴巴的
    private BrandService brandService;

    //查询所有
    @RequestMapping("/findAllBrands")
    public List<TbBrand> findAllBrands() throws Exception {
        return brandService.findAllBrands();
    }

    //分页查询
    @RequestMapping("/findByPage")
    public PageResult<TbBrand> findByPage(Integer currentPage, Integer pageSize) throws Exception {
        //分页查询，给前端返回一个PageResult<TbBrand>，有总记录数和每页数据
        return brandService.findByPage(currentPage, pageSize);
    }

    //新建一个品牌
    @RequestMapping("/addBrand")
    public ResultInfo addBrand(@RequestBody TbBrand tbBrand) throws Exception {
        if (tbBrand.getName() == null || tbBrand.getFirstChar() == null) {
            return new ResultInfo(false, "品牌或首字母不能为空");
        } else {
            try {
                //截取输入首字母框的第一个字符并转大写
                tbBrand.setFirstChar(tbBrand.getFirstChar().substring(0, 1).toUpperCase());
                brandService.addBrand(tbBrand);//添加成功
                return new ResultInfo(true, "新建成功");
            } catch (Exception e) {
                e.printStackTrace();
                //新建失败
                return new ResultInfo(false, "新建失败");
            }
        }
    }

    //修改品牌
    @RequestMapping("/updateBrand")
    public ResultInfo updateBrand(@RequestBody TbBrand tbBrand) throws Exception {
            if (tbBrand.getName()!= null && tbBrand.getFirstChar()!= null) {
                try {
                    //截取输入首字母框的第一个字符并转大写
                    tbBrand.setFirstChar(tbBrand.getFirstChar().substring(0, 1).toUpperCase());
                    brandService.updateBrand(tbBrand);
                    return new ResultInfo(true, "修改成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ResultInfo(false, "修改失败");
                }

            } else {
                return new ResultInfo(false, "品牌或首字母不能为空");
            }
    }

    //修改之前先要通过id找到要修改的对象
    @RequestMapping("/findBrandById")
    public TbBrand findBranById(Long id) throws Exception {
        return brandService.findBrandById(id);
    }

    //批量删除
    @RequestMapping("/deleteBrands")
    public ResultInfo deleteBrands(long[] ids) throws Exception {
        try {
            brandService.deleteBrands(ids);
            return new ResultInfo(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "删除失败");
        }
    }

    //条件分页查询,三个参数，第一个查询条件封装为TbBrand对象，当前页面和每页显示条数
    @RequestMapping("/search")
    public PageResult<TbBrand> findByPage(@RequestBody TbBrand tbBrand, Integer currentPage, Integer pageSize) throws Exception {

        return brandService.findByPage(tbBrand, currentPage, pageSize);
    }

    //模板管理的关联品牌
    @RequestMapping("/selectBrandList")
    public List<Map> selectBrandList(){
        //返回的json数据格式：{data:[{id:1,text:'联想'},{id:2,text:'华硕'},{id:3,text:'戴尔'},{id:4,text:"惠普"}]};
        return brandService.selectBrandList();
    }
}
