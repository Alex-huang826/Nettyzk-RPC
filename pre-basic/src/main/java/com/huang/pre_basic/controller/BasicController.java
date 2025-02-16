package com.huang.pre_basic.controller;

import com.huang.pre_basic.service.BasicService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.huang")
public class BasicController {
    public static void main(String[] args){
        ApplicationContext context = new AnnotationConfigApplicationContext(BasicController.class);
        //创建一个基于 Java 配置的 Spring 应用上下文（ApplicationContext）。
        //通过传入配置类 SpringServer.class，Spring 自动解析该配置类中的注解（如 @ComponentScan、@Bean），初始化容器。
        BasicService basicService = context.getBean(BasicService.class);//将 BasicService 的 Spring 管理实例注入到 basicService 变量中，便于后续调用。
        basicService.testSaveUser();//调用 testSaveUser 方法，实现用户保存相关的业务功能。
    }
}
