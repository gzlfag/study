package com.my.springmvc.controller;


import com.my.springmvc.annotation.Autowired;
import com.my.springmvc.annotation.Controller;
import com.my.springmvc.annotation.RequestMapping;
import com.my.springmvc.service.UserServie;

@Controller
public class UserController {

    @Autowired
    private UserServie userServie;

    @RequestMapping("addUser")
    public void addUser( ) {

        userServie.addUser();

    }
}
