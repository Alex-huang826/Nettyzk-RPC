package com.huang.netty.client;

import java.util.concurrent.atomic.AtomicLong;
//该类是客户端的请求封装类，用于表示客户端发送给服务端的每个请求。
//每个请求具有唯一的标识符（id）和请求内容（content）。
public class ClientRequest {
    private final long id;
    private Object content;
    private final AtomicLong aid = new AtomicLong(1);
    //用于生成全局唯一的 id 值。
    //AtomicLong 是线程安全的，可以在多线程环境下生成唯一的递增值。
    private String command;
    //command 是用来标识业务操作或方法的一种 命令字符串，通常被用作键值，用于在 Media.beanMap 中快速查找对应的方法和 Bean 实例。

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public ClientRequest(){//自动为其分配一个唯一的 id。
        id = aid.incrementAndGet();
    }
    public long getId(){
        return id;
    }

    public void setContent(Object content) {
        this.content = content;
    }
    public Object getContent(){
        return content;
    }
}
