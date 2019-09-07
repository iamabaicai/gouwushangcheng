package com.pinyougou.shop.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import java.util.ArrayList;
import java.util.List;

/**
 * 商家登陆认证服务
 */

public class UserDetailServiceImpl implements UserDetailsService {
    //远程注入
    //@Reference
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("登陆经过我验证了！");
        //构建角色列表
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        list.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        TbSeller seller = sellerService.findOne(username);
        if (seller!=null){
            User user = new User(seller.getSellerId(),seller.getPassword(),seller.getStatus().equals("1")?true:false
                    ,true,true,true,list);
            return user;
        }else {
            return null;
        }

    }
}
