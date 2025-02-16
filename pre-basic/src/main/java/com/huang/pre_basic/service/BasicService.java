package com.huang.pre_basic.service;

import com.alibaba.fastjson.JSONObject;
import com.huang.client.annotation.RemoteInvoke;
import com.huang.user.model.User;
import com.huang.user.remote.UserRemote;
import org.springframework.stereotype.Service;


@Service
public class BasicService {
    @RemoteInvoke
    private UserRemote userRemote;
    //这里的 userRemote 被标记，表明该字段将由动态代理生成一个远程服务代理对象，而不是手动实例化。
    public void testSaveUser(){
        User u = new User();
        u.setId(1);
        u.setName("黄泽龙");
        Object response = userRemote.saveUser(u);
        System.out.println(JSONObject.toJSONString(response));
    }
}
