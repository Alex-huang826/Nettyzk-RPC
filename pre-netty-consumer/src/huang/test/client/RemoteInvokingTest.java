package huang.test.client;

import com.alibaba.fastjson.JSONObject;
import com.huang.client.annotation.RemoteInvoke;
import com.huang.client.param.Response;
import com.huang.user.bean.User;
import com.huang.user.remote.UserRemote;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= RemoteInvokingTest.class)
@ComponentScan(basePackages = {"com.huang"})
public class RemoteInvokingTest {
    @RemoteInvoke
    private UserRemote userRemote;
    //这里的 userRemote 被标记，表明该字段将由动态代理生成一个远程服务代理对象，而不是手动实例化。

    @Test
    public void testSaveUser(){
        User u = new User();
        u.setId(1);
        u.setName("黄泽龙");
        Response response = userRemote.saveUser(u);
        System.out.println(JSONObject.toJSONString(response));
    }

    @Test
    public void testSaveUsers(){
        List<User> users = new ArrayList<User>();
        User u = new User();
        u.setId(1);
        u.setName("黄泽龙");
        users.add(u);
        userRemote.saveUsers(users);
    }
}
