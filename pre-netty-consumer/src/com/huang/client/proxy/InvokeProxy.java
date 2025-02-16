//通过标注 @RemoteInvoke 注解的字段，为其生成动态代理。
//拦截字段的方法调用，将其转换为远程调用请求，发送到远程服务。
package com.huang.client.proxy;

import com.huang.client.annotation.RemoteInvoke;
import com.huang.client.core.TcpClient;
import com.huang.client.param.ClientRequest;
import com.huang.client.param.Response;
import com.huang.user.bean.User;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class InvokeProxy implements BeanPostProcessor {//实现了 BeanPostProcessor 接口，用于在 Spring Bean 初始化前后执行特定的逻辑。
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {//在 Bean 初始化之前执行
        Field[] fields = bean.getClass().getDeclaredFields();//获取当前 Bean 的所有字段，包括私有字段。
        for(Field field: fields){
            if(field.isAnnotationPresent(RemoteInvoke.class)){
                //检查字段是否标注了 @RemoteInvoke 注解。只有被 @RemoteInvoke 标记的字段会被动态代理处理。
                field.setAccessible(true);// 设置字段可访问
                final Map<Method,Class>methodClassMap= new HashMap<Method,Class>();//创建一个 Map，用于存储字段类型的所有方法及其所属的接口类型。
                putMethodClass(methodClassMap,field);//将字段的所有方法及其接口类型存入 methodClassMap，方便后续构造远程调用请求。
                Enhancer enhancer = new Enhancer();// 创建 CGLIB 动态代理对象
                enhancer.setInterfaces(new Class[]{field.getType()});// 设置代理对象的接口
                enhancer.setCallback(new MethodInterceptor() {// 设置方法拦截器
                    @Override
                    public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                        //采用netty客户端需要去调用服务器，构造远程调用请求
                        ClientRequest request = new ClientRequest();
                        request.setCommand(methodClassMap.get(method).getName()+"."+method.getName());
                        request.setContent(args[0]);
                        Response resp = TcpClient.send(request);// 通过 TcpClient 发送请求
                        return resp;
                    }
                });
                try {
                    field.set(bean,enhancer.create());// 将代理对象注入字段
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }


    //对属性的所有方法和属性接口类型放入到一个Map中
    private void putMethodClass(Map<Method, Class> methodClassMap, Field field) {
        Method[] methods = field.getType().getDeclaredMethods();// 获取字段类型的所有方法
        for(Method m: methods){
            methodClassMap.put(m,field.getType());// 将方法及其接口类型放入Map
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
