package com.study.mapping;

import java.lang.reflect.Method;

/**
 * 将控制器实例和方法封装到Handler对象里面
 */
public class Handler {
    private Object bean;
    private Method method;

    public Handler() {
    }

    public Handler(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
