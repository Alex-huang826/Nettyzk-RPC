//Netty实战高性能分布式RPC
package com.huang.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//通过 Java 配置方式 初始化 Spring 容器。
//使用 @ComponentScan 扫描指定包中的组件，将标注了 @Component、@Service、@Repository、@Controller 等注解的类注册为 Spring Bean。
//通过 main 方法启动 Spring 应用。
@Configuration
@ComponentScan("com.huang")
//指定 Spring 容器扫描的基础包路径（com.huang）
//Spring 会扫描该包及其子包中的所有类，并自动注册标注了特定注解（如 @Component, @Service, @Controller, @Repository）的类为 Spring Bean。

public class SpringServer {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringServer.class);
        //创建一个基于 Java 配置的 Spring 应用上下文（ApplicationContext）。
        //通过传入配置类 SpringServer.class，Spring 自动解析该配置类中的注解（如 @ComponentScan、@Bean），初始化容器。
    }
}
