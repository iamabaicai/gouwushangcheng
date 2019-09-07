package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * 搜索关键字搜索商品
     * @param searchMap
     * @return
     */
    public Map search(Map searchMap);

    /**
     * 导入数据
     * @param list
     */
    public void importList(List list);

    /**
     * 删除数据,运营商后台逻辑删除商品，从索引库删除该商品的所有SKU
     * @param goodsIdList
     */
    public void deleteByGoodsIds(List goodsIdList);


    void saveItemsToSolr(List<TbItem> tbItems);

    public void deleteByGoodsId(Long[] ids);
}
