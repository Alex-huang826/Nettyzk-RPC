package com.huang.client.param;
//这是一个用于封装服务端返回响应数据的类。
//它的设计目标是为客户端提供一个统一的数据结构，存储服务端返回的结果以及对应请求的标识符（id）。
public class Response {
    private Long id;
    private Object result;
    private String code="00000";//00000表示成功，其他表示失败
    private String msg;//失败的原因

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
