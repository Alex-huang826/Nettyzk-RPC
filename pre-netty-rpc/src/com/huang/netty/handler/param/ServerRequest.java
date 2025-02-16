package com.huang.netty.handler.param;
//ServerRequest 是一个简单的 POJO（Plain Old Java Object），用于封装服务器接收到的客户端请求数据。
//它包含两个字段：id 和 content，分别表示请求的唯一标识符和请求的内容
public class ServerRequest {
    private Long id;
    private Object content;
    private String command;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
