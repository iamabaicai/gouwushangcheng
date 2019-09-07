package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.GoodsService;
import entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;


import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbSellerMapper sellerMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public void add(Goods goods) {
        //未申请状态
        goods.getGoods().setAuditStatus("0");
        //插入商品基本信息
        goodsMapper.insert(goods.getGoods());
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());//设置ID
        //插入商品的详细信息
        goodsDescMapper.insert(goods.getGoodsDesc());
        //插入商品SKU列表到tb_item
        saveItemList(goods);

    }

    //抽取插入SKU列表的代码
    private void saveItemList(Goods goods){
        //插入SKU列表，是否启用规格
        //如果启用则有多个SKU
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            for (TbItem item : goods.getItemList()) {
                //标题是商品名称+商品规格选项(移动3G 16G)"spec":{"网络":"移动3G","机身内存":"16G"}
                String title = goods.getGoods().getGoodsName();
                Map<String, Object> specMap = JSON.parseObject(item.getSpec());
                for (String key : specMap.keySet()) {
                    title += " " + specMap.get(key);
                }
                item.setTitle(title);//名称
                item.setGoodsId(goods.getGoods().getId());//商品id
                item.setSellerId(goods.getGoods().getSellerId());//卖家id
                item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类三级id
                item.setCreateTime(new Date());//创建时间
                item.setUpdateTime(new Date());//修改日期
                //品牌名称
                TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
                item.setBrand(brand.getName());
                //分类名称
                TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
                item.setCategory(tbItemCat.getName());
                //商家名称
                TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
                item.setSeller(tbSeller.getNickName());
                //图片地址（取spu的第一个图片）
                List<Map> maps = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
                if (maps.size() > 0) {
                    item.setImage((String) maps.get(0).get("url"));
                }
                itemMapper.insert(item);
            }
        }else {
            //如果不启用规格该SPU只有一个SKU
            TbItem item = new TbItem();
            item.setTitle(goods.getGoods().getGoodsName());//商品名称
            item.setGoodsId(goods.getGoods().getId());//商品id
            item.setSellerId(goods.getGoods().getSellerId());//卖家id
            item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类三级id
            item.setCreateTime(new Date());//创建时间
            item.setUpdateTime(new Date());//修改日期
            //品牌名称
            TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
            item.setBrand(brand.getName());
            //分类名称
            TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
            item.setCategory(tbItemCat.getName());
            //商家名称
            TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
            item.setSeller(tbSeller.getNickName());
            //图片地址（取spu的第一个图片）
            List<Map> maps = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
            if (maps.size() > 0) {
                item.setImage((String) maps.get(0).get("url"));
            }
            item.setPrice(goods.getGoods().getPrice());//价格
            item.setStatus("1");//状态
            item.setIsDefault("1");//是否默认
            item.setNum(99999);//库存数量
            item.setSpec("{}");

            itemMapper.insert(item);
        }

    };



    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        //设置商品未申请状态
        goods.getGoods().setAuditStatus("0");
        //保存商品表
        goodsMapper.updateByPrimaryKey(goods.getGoods());
        //保存商品扩展表
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());

        //删除原来的SKU列表
        TbItemExample example =new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getGoods().getId());
        itemMapper.deleteByExample(example);

        //重新添加商品SKU列表
        saveItemList(goods);

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();//商品基本信息
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);//商品扩展信息
        TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(goodsDesc);

        //商品SKU列表
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);//通过外键商品id查询SKU列表
        List<TbItem> itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);
        return goods;
    }

    /**
     * 批量删除
     * 用户选中部分商品，点击删除按钮即可实现商品删除。注意，
     * 这里的删除并非是物理删除，而是修改tb_goods表的is_delete字段为1 ，
     * 我们可以称之为“逻辑删除”
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete("1");

            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsDeleteIsNull();//非删除状态的商品
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                //这里只能精确查询，不能模糊查询
                //criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
                criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
            }


        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 运营商管理后台商品审核
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids,String status){
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

    /**
     * 根据商品ID和状态查询Item表信息
     * @param goodsId
     * @param status
     * @return
     */
    @Override
    public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(status);
        //Arrays.asList(goodsIds)将数组转化为集合
        criteria.andGoodsIdIn(Arrays.asList(goodsIds));

        return itemMapper.selectByExample(example);
    }

}
