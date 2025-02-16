//package com.huang.user.remote;
//
//import com.huang.netty.annotation.Remote;
//import com.huang.netty.util.Response;
//import com.huang.netty.util.ResponseUtil;
//import com.huang.user.bean.User;
//import com.huang.user.service.UserService;
//
//import javax.annotation.Resource;
//import java.util.List;
//
//@Remote
//public class UserRemoteImpl implements UserRemote{
//    @Resource
//    private UserService userService;
//    public Response saveUser(User user){
//        userService.save(user);
//        Response response = ResponseUtil.createSuccessResult(user);
//        System.out.println("返回响应: " + response);
//        return response;
//    }
//    public Response saveUsers(List<User> users){
//        userService.saveList(users);
//        return ResponseUtil.createSuccessResult(users);
//    }
//}
