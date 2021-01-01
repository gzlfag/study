package com.example.spring.others;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * @author majie
 * @date 2018-03-24
 */
@Service
public class ListenerService {

    @EventListener(classes = {ApplicationEvent.class})
    public void listener(ApplicationEvent event) {
        System.out.println("ListenerService监听到的事件" + event);
    }
}
