package com.huang.user.remote;

import com.huang.client.param.Response;
import com.huang.user.bean.User;

import java.util.List;

public interface UserRemote {
    public Response saveUser(User user);
    public Response saveUsers(List<User> users);
}
