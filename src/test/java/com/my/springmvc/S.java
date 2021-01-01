package com.my.springmvc;

import java.util.*;

/**
 * @author majie
 * @date 2018-03-25
 */
public class S {

    public static void main(String[] args) {
        Set set = new TreeSet();
        List list = new ArrayList();
        for (int i = -3; i < 3; i++) {
            set.add(i);
            list.add(i);
        }

        System.out.println(set);
        System.out.println(list);

        for (int i = 0; i < 3; i++) {
            set.remove(i);
            list.remove(i);
        }

        System.out.println(set);
        System.out.println(list);

        for (int i = 0; i < list.size(); i++) {
            list.remove(i);
        }

        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            boolean remove = list.remove(iterator.next());

        }
        System.out.println(list);
    }
}
