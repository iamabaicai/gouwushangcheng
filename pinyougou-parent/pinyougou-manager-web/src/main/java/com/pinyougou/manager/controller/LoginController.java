package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/getLoginName")
    public Map getLoginName() {
        //获取登陆用户的名称
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        HashMap hp = new HashMap();
        hp.put("loginName",loginName);
        return hp;
    }
}
