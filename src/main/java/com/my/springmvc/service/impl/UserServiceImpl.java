package com.my.springmvc.service.impl;


import com.my.springmvc.annotation.Service;
import com.my.springmvc.service.UserServie;

@Service("userServie")
public class UserServiceImpl implements UserServie {

    public void addUser( ) {
        System.out.println("调用了UserServie的addUser方法");
    }

    public void deleteUser( ) {
        System.out.println("调用了UserServie的deleteUser方法");
    }

    public void updateUser( ) {
        System.out.println("调用了UserServie的updateUser方法");
    }

    public void queryUser( ) {
        System.out.println("调用了UserServie的queryUser方法");
    }

}
