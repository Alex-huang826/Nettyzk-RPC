package com.huang.netty.medium;

import com.huang.netty.annotation.Remote;
import com.huang.netty.annotation.RemoteInvoke;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class InitialMedium implements BeanPostProcessor {
//InitialMedium 类实现了 Spring 的 BeanPostProcessor 接口，用于在 Spring 容器初始化 bean 的过程中添加自定义逻辑。
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    //这是 BeanPostProcessor 的一个回调方法，在 Spring 容器完成对一个 Bean 的初始化之后调用。
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(Remote.class)){//检查 bean 的类是否被 @Controller 注解标注。
            Method[] methods = bean.getClass().getDeclaredMethods();//获取类的所有方法（包括私有方法）。
            for(Method m:methods){//遍历每个方法
                String key = bean.getClass().getInterfaces()[0].getName()+"."+m.getName();//构造方法的唯一标识符，用于在 Media.beanMap 中存储和查找方法。
                Map<String,BeanMethod> beanMap = Media.beanMap;//获取 Media 类中定义的全局映射 beanMap。
                BeanMethod beanMethod = new BeanMethod();//创建一个 BeanMethod 对象，用于封装当前方法与对应的 Bean 实例。
                beanMethod.setBean(bean);//设置当前方法所属的 Bean 实例。
                beanMethod.setMethod(m);//设置当前方法的反射对象（Method）。
                beanMap.put(key,beanMethod);//将方法和对应的 BeanMethod 对象注册到全局映射 beanMap 中。
                //后续可以通过 key（方法标识符）快速找到对应的方法和 Bean 实例。
            }
        }
        return bean;
    }
}
