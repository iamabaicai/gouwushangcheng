package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.GoodsService;
import entity.Goods;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Goods goods) {
        //获取登陆商家的名称
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        //设置商品所属商家名称(商家id)
        goods.getGoods().setSellerId(sellerId);
        try {
            goodsService.add(goods);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改商品信息
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Goods goods) {
        //安全判断当前商品所属商家id
        String sellerId1 = goods.getGoods().getSellerId();
        //当前登录用户的用户名也是用户Id
        String sellerId2 = SecurityContextHolder.getContext().getAuthentication().getName();
        //校验是否是当前商家的id
        String sellerId3 = goodsService.findOne(goods.getGoods().getId()).getGoods().getSellerId();
        if (!sellerId2.equals(sellerId3) || !sellerId2.equals(sellerId1)) {
            return new Result(false, "非法操作");
        }

        try {
            goodsService.update(goods);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return 商品修改数据回显
     */
    @RequestMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findOne(id);
    }

    /**
     * 批量删除
     *逻辑删除，修改商品SKU列表tb_item表的is_delete字段为1
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.delete(ids);
            //从索引库删除商品
            itemSearchService.deleteByGoodsIds(Arrays.asList(ids));

            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        //设置商家id，商家后台只能看到属于自己的商品
//		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
//		goods.setSellerId(sellerId);
        return goodsService.findPage(goods, page, rows);
    }

    /**
     * 运营商管理后台商品审核
     *
     * @param ids 商id
     * @param status 审核后修改商品状态
     * ['未审核'/0,'已审核'/1,'审核未通过'/2,'已关闭'/3];
     */
    @Reference
    private ItemSearchService itemSearchService;

    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);
            if ("1".equals(status)) {
                List<TbItem> itemList = goodsService.findItemListByGoodsIdandStatus(ids, status);

                //调用搜索接口实现数据批量导入
                if(itemList.size()>0){
                    itemSearchService.importList(itemList);
                    System.out.println("数据导入索引库");
                }else {
                    System.out.println("没有数据明细");
                }
            }
            return new Result(true, "审核完成");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "审核失败");
        }
    }

}
