package com.spring.transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional
    public void insertUser( ) {
        userDao.insert();
        //otherDao.other();xxx
        System.out.println("插入完成..");
        int i = 10 / 0;    //抛出异常回滚
    }

}
