package com.huang.user.remote;

import com.huang.netty.annotation.Remote;
import com.huang.netty.util.ResponseUtil;
import com.huang.user.model.User;
import com.huang.user.service.UserService;

import javax.annotation.Resource;
import java.util.List;

@Remote
public class UserRemoteImpl implements UserRemote{
    @Resource
    private UserService userService;
    public Object saveUser(User user){
        userService.save(user);
        return ResponseUtil.createSuccessResult(user);
    }
    public Object saveUsers(List<User> users){
        userService.saveList(users);
        return ResponseUtil.createSuccessResult(users);
    }
}
