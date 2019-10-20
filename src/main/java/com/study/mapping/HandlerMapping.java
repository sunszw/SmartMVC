package com.study.mapping;

import com.study.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 映射处理器：负责提供请求路径与控制器实例和方法的对应关系
 */
public class HandlerMapping {

    private Map<String, Handler> maps = new HashMap<>();

    public void process(List<Object> beans) {
        for (Object bean : beans) {
            Class cls = bean.getClass();
            Method[] methods = cls.getDeclaredMethods();
            RequestMapping classMapping = (RequestMapping) cls.getAnnotation(RequestMapping.class);
            String classPath = "";
            if (classMapping != null) {
                //获得类上的注解
                classPath = classMapping.value();
            }

            //遍历控制器中的方法
            for (Method method : methods) {
                //获得注解
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                //获得方法上的注解
                String methodPath = requestMapping.value();
                //获得完整请求路径
                String path = "";
                if (classMapping == null) {
                    if (methodPath.contains("/")) {
                        path = methodPath;
                    } else {
                        path = "/" + methodPath;
                    }
                } else {
                    if (classPath.contains("/") && methodPath.contains("/")) {
                        path = classPath + methodPath;
                    } else if (classPath.contains("/") && !methodPath.contains("/")) {
                        path = classPath + "/" + methodPath;
                    } else if (!classPath.contains("/") && methodPath.contains("/")) {
                        path = "/" + classPath + methodPath;
                    } else {
                        path = "/" + classPath + "/" + methodPath;
                    }
                }
                Handler handler = new Handler(bean, method);
                //将请求路径保存到maps中
                maps.put(path, handler);
            }

        }
    }

    /**
     * 根据请求路径返回handler对象
     *
     * @return
     */
    public Handler getHandler(String path) {
        return maps.get(path);
    }
}
