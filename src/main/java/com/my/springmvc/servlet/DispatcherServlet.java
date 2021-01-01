package com.my.springmvc.servlet;

import com.my.springmvc.annotation.Autowired;
import com.my.springmvc.annotation.Controller;
import com.my.springmvc.annotation.RequestMapping;
import com.my.springmvc.annotation.Service;
import com.my.springmvc.controller.UserController;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/DispatcherServlet")
public class DispatcherServlet extends HttpServlet {

    // 用来存放扫描到的权限定类名
    private List<String> packageName = new ArrayList<>();

    // 存放bean的map
    private Map<String, Object> map = new HashMap<>();

    //url对应的menthod
    private Map<String, Method> handlerMaps = new HashMap<>();

    /**
     * 1.首先会扫描基包上的注解:spring
     * 2.扫描我们的基础包之后拿到我们的权限定名称 包名称+类名
     * spring/controller/UserController.java
     * 3.替换/为. 去掉后缀
     */
    public void init( ) throws ServletException {
        // 扫描基础包
        scanBase("com.my.spring");

        // 将注解上的实例放入IOC中
        try {
            filterAndInstance();
            springIOC();
            handlerMappers();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 扫描基础包
     *
     * @param basePackage
     */
    private void scanBase(String basePackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + replacePath(basePackage));
        // 拿到该路径的文件或者文件夹
        String pathFile = url.getFile();
        // 最终将这个路径封装成file类
        File file = new File(pathFile);

        String[] fileList = file.list();
        for (String string : fileList) {
            // 再次构造成一个file
            File file2 = new File(string);
            if (file2.isDirectory()) {
                // 递归扫描
                scanBase(basePackage + "." + file2.getName());
            } else if (file2.isFile()) {
                // 表示扫描的是一个文件
                packageName.add(basePackage + "." + file2.getName());
            }
        }
    }

    /**
     * 将包名替换成路径
     */
    private String replacePath(String path) {
        return path.replace("\\.", "/");
    }

    /**
     * 拦截方法请求,然后在对应的请求地址上获取对应的实例
     */
    private void filterAndInstance( ) throws Exception {
        // 判断是否有实例
        if (packageName.size() <= 0) {
            return;
        }
        for (String className : packageName) {
            Class<?> classz = Class.forName(className.replace(".class", ""));
            if (classz.isAnnotationPresent(Controller.class)) {
                Object newInstance = classz.newInstance();
                // 将实例放入map key beanId value newInstance
                Controller controller = classz.getAnnotation(Controller.class);
                // 通过注解对象拿到属性值
                String key = controller.value();
                // 这里只是简单模拟
                if (map.containsKey(key)) {
                    throw new Exception("id已存在");
                } else {
                    map.put(key, newInstance);
                }
            } else if (classz.isAnnotationPresent(Service.class)) {
                Object newInstance = classz.newInstance();
                // 将实例放入map key beanId value newInstance
                Service service = classz.getAnnotation(Service.class);
                // 通过注解对象拿到属性值
                String key = service.value();
                // 这里只是简单模拟
                if (map.containsKey(key)) {
                    throw new Exception("id已存在");
                } else {
                    map.put(key, newInstance);
                }
            } else {
                // 没有注解
                continue;
            }
        }
    }

    // 将实例注入到spring容器中
    private void springIOC( ) throws Exception {
        if (packageName.size() <= 0) {
            return;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    //注入
                    String value = ((Autowired) field.getAnnotation(Autowired.class)).value();
                    //防止有私有变量
                    field.setAccessible(true);
                    field.set(entry.getValue(), map.get(value));
                } else {
                    continue;
                }
            }
        }
    }

    /**
     * 通过url找到相应的menthod对象进行处理
     */
    private void handlerMappers( ) throws Exception {
        if (packageName.size() <= 0) {
            return;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            //如果有control的注解
            if (entry.getValue().getClass().isAnnotationPresent(Controller.class)) {
                String baseUrl = ((Controller) entry.getValue().getClass().getAnnotation(Controller.class)).value();
                Method[] controllerMethods = entry.getValue().getClass().getMethods();
                for (Method method : controllerMethods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        String methodUrl = ((RequestMapping) entry.getValue().getClass().getAnnotation(RequestMapping.class)).value();
                        handlerMaps.put("/" + baseUrl + "/" + methodUrl, method);
                    } else {
                        continue;
                    }
                }
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //拿到完整的路径
        String requestURI = req.getRequestURI();
        String projectName = req.getContextPath();
        //baseUrl + methodurl
        String path = requestURI.replace(projectName, "");
        //方法对象
        Method method = handlerMaps.get(path);
        if (method == null) {
            PrintWriter pw = resp.getWriter();
            pw.write("路径不存在");
            return;
        }
        //localhost:8080/study/spring/uri/
        String className = requestURI.split("/")[2];
        UserController userController = (UserController) map.get(className);
        try {
            method.invoke(userController, new Object[]{req, resp, null});
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
