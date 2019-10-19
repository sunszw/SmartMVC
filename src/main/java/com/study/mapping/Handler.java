package com.study.mapping;

import java.lang.reflect.Method;

/**
 * 将控制器实例和方法封装到Handler对象里面
 */
public class Handler {
    private Object object;
    private Method method;

    public Handler() {
    }

    public Handler(Object object, Method method) {
        this.object = object;
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
