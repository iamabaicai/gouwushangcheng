package com.pinyougou.solr.utils;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Map;

@Component
public class SolrUtils {

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 初始化项目，只执行一次，把数据库数据批量导入solr索引库
     */
    public void importData() {
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andStatusEqualTo("1");//查询启用的规格
        List<TbItem> itemList = tbItemMapper.selectByExample(tbItemExample);
        System.out.println(itemList.size());
        for (TbItem tbItem : itemList) {
            System.out.println(tbItem.getId() + "--" + tbItem.getBrand());
            //specMap的处理
            String spec = tbItem.getSpec();
            if (spec != null) {
                Map map = JSON.parseObject(spec, Map.class);
                tbItem.setSpecMap(map);
            }
        }

        //批量将数据库数据导入到索引库
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }


}
