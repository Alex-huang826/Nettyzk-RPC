//这段代码实现了一个基于 Netty 的 TCP 长连接客户端：
//Netty 的所有 I/O 操作（如连接、写入、关闭等）都是异步的，
//它们不会立即返回结果，而是返回一个 ChannelFuture 对象，用于表示该操作的当前状态或最终结果。
package com.huang.netty.client;

import com.alibaba.fastjson.JSONObject;
import com.huang.netty.handler.SimpleClientHandler;
import com.huang.netty.util.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class TcpClient {
    static final Bootstrap b = new Bootstrap();//Bootstrap 是 Netty 提供的客户端启动器，用于配置和启动客户端
    static ChannelFuture f = null;//f 表示连接到服务端的通道（Channel）操作结果。
    //ChannelFuture 是异步操作的结果对象，用于跟踪连接的状态。
    static {
        EventLoopGroup workerGroup = new NioEventLoopGroup();//创建一个工作线程组，负责处理 I/O 操作。
        // NioEventLoopGroup 是 Netty 提供的线程池实现，基于 Java NIO。
        Bootstrap b = new Bootstrap(); //初始化一个 Bootstrap 实例，用于配置客户端。
        b.group(workerGroup); //将工作线程组绑定到 Bootstrap。
        b.channel(NioSocketChannel.class); //端使用的通道类型为 NioSocketChannel，即基于 NIO 的客户端通道。
        b.option(ChannelOption.SO_KEEPALIVE, true); //配置通道选项，启用 TCP 的 SO_KEEPALIVE，保持连接活跃。
        //SO_KEEPALIVE 是底层 TCP 协议的特性，常用于长连接。
        b.handler(new ChannelInitializer<SocketChannel>() {//设置通道的初始化逻辑：
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
                //使用分隔符解码器，基于行分隔符（\r\n）解析消息。
                ch.pipeline().addLast(new StringDecoder());//将字节流解码为字符串
                ch.pipeline().addLast(new SimpleClientHandler());//添加自定义的客户端处理器，用于处理业务逻辑。
                ch.pipeline().addLast(new StringEncoder());//将字符串编码为字节流。

            }
        });
        String host = "localhost";//设置目标服务器的主机地址为本地服务器 (localhost)。
        int port = 8080;//定义目标服务器的端口号为 8080。
        try {
            f = b.connect(host, port).sync();//调用 connect 方法连接到指定的服务器地址和端口，返回一个 ChannelFuture。
            //使用 sync 方法阻塞当前线程，直到连接成功。
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //发送数据并获取响应。
    //注意长连接中，每一个请求都是同一个连接，存在并发问题要注意区分不同的请求
    public static Response send(ClientRequest request){
        f.channel().writeAndFlush(JSONObject.toJSONString(request));//// 将请求对象转换为 JSON 字符串发送。
        f.channel().writeAndFlush("\r\n");
        //通过通道发送数据，首先将请求对象序列化为 JSON 字符串，然后发送分隔符 \r\n。
        //分隔符是为了让服务端知道数据包的边界。
        DefaultFuture df = new DefaultFuture(request);//创建一个 DefaultFuture 对象，用于异步处理当前请求的响应。
        System.out.println("请求已发送，等待响应...");
        return df.get();//调用 df.get() 阻塞当前线程，直到获取到响应结果。

    }
}
