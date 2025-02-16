package com.huang.netty.medium;

import java.lang.reflect.Method;
//该代码定义了一个名为 BeanMethod 的类，用于封装一个 Java Bean 和其对应的方法（Method），以支持反射调用。
public class BeanMethod {
    private Object bean;
    private Method method;

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
