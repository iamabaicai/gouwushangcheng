package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 搜索条件用map封装，返回值也用map封装
     * searchMap={cateroy:'',brand:'',spec:{}}
     * 搜索关键字搜索商品
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map search(Map searchMap) {
        //搜索关键字去除空格，有空格ik中文分词起识别不了
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));

        //初始化返回值
        Map map = new HashMap();

//        Query query=new SimpleQuery();
//        //添加查询条件
//        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
//        query.addCriteria(criteria);
//        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);

        //1.按关键字查询列表（高亮显示）
        map.putAll(searchList(searchMap));

        //2.分组查询 商品分类列表
        List categoryList = getCategoryList(searchMap);
        map.put("categoryList", categoryList);
        //3.查询品牌和规格列表
        String category = (String) searchMap.get("category");
        if (!"".equals(category)) {
            map.putAll(searchBrandAndSpecList(category));
        } else {
            if (categoryList.size() > 0) {
                map.putAll(searchBrandAndSpecList((String) categoryList.get(0)));
            }
        }

        return map;
    }

    @Override
    public void importList(List list) {
        //更新之后需要提交到索引库
        solrTemplate.saveBeans(list);
        solrTemplate.commit();

    }

    /**
     * 删除数据,运营商后台逻辑删除商品，从索引库删除该商品的所有SKU
     * @param goodsIdList
     */
    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        //从索引库删除商品列表
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    private Map searchList(Map searchMap) {
        HashMap hm = new HashMap();
        //高亮选项初始化******************************
        HighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//高亮域
        //高亮前后缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        //为查询对象设置高亮选项
        query.setHighlightOptions(highlightOptions);

        //1.1 关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //1.2 按商品分类过滤
        if (!"".equals(searchMap.get("category")) && searchMap.get("category") != null) {
            //如果用户选择了分类
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //1.3 按品牌过滤
        if (!"".equals(searchMap.get("brand")) && searchMap.get("brand") != null) {
            //如果用户选择了品牌
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.4 按规格过滤
        if (!"".equals(searchMap.get("spec")) && searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {

                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);

            }

        }
        //1.5按价格过滤筛选,前端相应的价格数字符串“0-500”
        if (!"".equals(searchMap.get("price")) && searchMap.get("price") != null) {
            String[] prices = searchMap.get("price").toString().split("-");
            if (!"".equals(prices[0])) {
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!"*".equals(prices[1])) {
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

        }
        //1.6分页查询
        Integer pageNum = (Integer) searchMap.get("pageNum");//获取页码
        if (pageNum == null) {
            pageNum = 1;//给页码默认值1
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");//每页显示记录条数
        if (pageSize == null) {
            pageSize = 30;//默认每页显示记录条数20
        }
        //设置分页参数
        query.setOffset((pageNum - 1) * pageSize);//起始索引
        query.setRows(pageSize);///每页显示记录条数

        //1.7按价格排序
        //排序字段
        String sortField = (String) searchMap.get("sortField");
        //asc升序 desc降序
        String sort = (String) searchMap.get("sort");
        if (sort != null && !"".equals(sort)) {
            if (sort.equals("ASC")) {
                //升序
                Sort orders = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(orders);
            }
            if (sort.equals("DESC")) {
                Sort orders = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(orders);
            }
        }

        //******************获取高亮结果集***************************
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
        for (HighlightEntry<TbItem> tbItemHighlightEntry : highlighted) {
            //获取包含高亮的商品
            TbItem tbItem = tbItemHighlightEntry.getEntity();

            //由于每个商品的高亮字段可能有多个
            //每个高亮中的信息可能有多个。
            List<HighlightEntry.Highlight> highlights = tbItemHighlightEntry.getHighlights();
            if (highlights != null && highlights.size() > 0) {
                HighlightEntry.Highlight s = tbItemHighlightEntry.getHighlights().get(0);
                if (s.getSnipplets() != null && s.getSnipplets().size() > 0) {
                    String s1 = s.getSnipplets().get(0);
                    tbItem.setTitle(s1);
                }
            }

        }
        hm.put("totalPages", page.getTotalPages());//总页数
        hm.put("totalElements", page.getTotalElements());//总记录条数
        hm.put("rows", page.getContent());//每页记录数据
        return hm;
    }


    /**
     * 获取满足条件的分类名称
     *
     * @return
     */
    public List<String> getCategoryList(Map searchMap) {
        List<String> list = new ArrayList<>();

        Query query = new SimpleQuery("*:*");
        //设置搜索的条件关键字
        //关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //设置分组的域
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        //得到分组的页
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        //获取item_category域分组的结果
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
        //得到分组结果的入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            //将分组结果的名称封装到返回值中
            list.add(entry.getGroupValue());
        }
        return list;
    }

    /**
     * 查询品牌和规格列表
     *
     * @param category 分类名称
     * @return
     */
    private Map searchBrandAndSpecList(String category) {
        Map map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);//获取模板ID
        if (typeId != null) {
            //根据模板ID查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);//返回值添加品牌列表
            //根据模板ID查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
        return map;
    }

    @Override
    public void saveItemsToSolr(List<TbItem> tbItems) {

    }

    @Override
    public void deleteByGoodsId(Long[] ids) {

    }
}
