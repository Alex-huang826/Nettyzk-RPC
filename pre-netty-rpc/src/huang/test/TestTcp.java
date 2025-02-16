//package huang.test;
//
//import com.huang.netty.client.ClientRequest;
//import com.huang.netty.util.Response;
//import com.huang.netty.client.TcpClient;
//import com.huang.user.bean.User;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TestTcp {//通过模拟客户端发送请求，测试服务器的处理逻辑，并检查服务器返回的响应是否符合预期。
//    @Test
//    public void testGetResponse(){
//        ClientRequest request = new ClientRequest();//创建一个客户端请求对象 request。
//        request.setContent("测试tcp长连接请求");//为请求对象设置具体的内容，即发送给服务器的数据。
//        Response resp = TcpClient.send(request);//调用 TcpClient.send() 方法，将 request 发送到服务器，并等待响应。
//        //返回值 resp 是一个 Response 对象，封装了服务器返回的响应数据。
//        //具体流程：
//        //调用 send() 方法：通过 Netty 的 TCP 长连接，将 request 转换为 JSON 字符串并发送给服务器。
//        //使用 DefaultFuture 等机制，阻塞当前线程，等待服务器返回响应。
//        //等待响应：客户端线程在调用 send() 后会等待服务器的响应。一旦服务器返回响应，通过 DefaultFuture.receive() 方法唤醒等待的线程，并返回 Response 对象。
//        //返回 Response：send() 方法返回的 Response 对象包含两个字段：id：对应请求的唯一标识符。result：服务器返回的具体处理结果。
//        System.out.println(resp.getResult());
//        //获取服务器返回的结果，并打印到控制台。
//    }
//    @Test
//    public void testSaveUser(){
//        ClientRequest request = new ClientRequest();
//        User u = new User();
//        u.setId(1);
//        u.setName("黄泽龙");
//        request.setCommand("com.huang.user.Controller.UserController.saveUser");
//        request.setContent(u);
//        Response resp = TcpClient.send(request);
//        System.out.println(resp.getResult());
//    }
//
//    @Test
//    public void testSaveUsers(){
//        ClientRequest request = new ClientRequest();
//        List<User> users = new ArrayList<User>();
//        User u = new User();
//        u.setId(1);
//        u.setName("黄泽龙");
//        users.add(u);
//        request.setCommand("com.huang.user.Controller.UserController.saveUsers");
//        request.setContent(users);
//        Response resp = TcpClient.send(request);
//        System.out.println(resp.getResult());
//    }
//}
