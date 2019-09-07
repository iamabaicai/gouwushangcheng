package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/loginName")
    public Map getLoginName(){
        //获取登陆用户名称
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        HashMap hm = new HashMap();
        hm.put("loginName",loginName);
        return hm;
    }
}
