package com.pinyougou.search.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemSearch")
public class ItemSearchController {

    /**
     * 搜索条件用map封装，返回值也用map封装
     * 搜索关键字搜索商品
     * @param searchMap
     * @return
     */
    @Reference(timeout = 5000)
    public ItemSearchService itemSearchService;

    @RequestMapping("/search")
    public Map search(@RequestBody  Map searchMap) {
        Map search = itemSearchService.search(searchMap);
//        System.out.println(search);
        return search;
    }

}
