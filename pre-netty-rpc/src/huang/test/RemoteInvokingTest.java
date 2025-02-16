//package huang.test;
//
//import com.huang.netty.annotation.Remote;
//import com.huang.netty.annotation.RemoteInvoke;
//import com.huang.netty.client.ClientRequest;
//import com.huang.netty.client.TcpClient;
//import com.huang.netty.util.Response;
//import com.huang.user.bean.User;
//import com.huang.user.remote.UserRemote;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class RemoteInvokingTest {
//    @RemoteInvoke
//    private UserRemote userRemote;
//
//    @Test
//    public void testSaveUser(){
//        User u = new User();
//        u.setId(1);
//        u.setName("黄泽龙");
//        userRemote.saveUser(u);
//    }
//
//    @Test
//    public void testSaveUsers(){
//        List<User> users = new ArrayList<User>();
//        User u = new User();
//        u.setId(1);
//        u.setName("黄泽龙");
//        users.add(u);
//        userRemote.saveUsers(users);
//    }
//}
