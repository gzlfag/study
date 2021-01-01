package com.example.myspringmvc.controller;


import com.example.myspringmvc.annotation.Autowired;
import com.example.myspringmvc.annotation.Controller;
import com.example.myspringmvc.annotation.RequestMapping;
import com.example.myspringmvc.service.UserServie;

@Controller
public class UserController {

    @Autowired
    private UserServie userServie;

    @RequestMapping("addUser")
    public void addUser( ) {

        userServie.addUser();

    }
}
