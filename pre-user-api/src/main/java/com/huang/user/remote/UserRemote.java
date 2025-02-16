package com.huang.user.remote;

import com.huang.user.model.User;

import java.util.List;

public interface UserRemote {
    public Object saveUser(User user);
    public Object saveUsers(List<User> users);
}
