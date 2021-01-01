package com.example.spring.others;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author majie
 * @date 2018-03-24
 */
@Component
public class MyEventListener implements ApplicationListener<ApplicationEvent> {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {


        System.out.println("监听到事件:" + event);
    }
}
