//基于netty框架的客户端程序
package com.huang.netty.client;

import com.huang.netty.handler.SimpleClientHandler;
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
import io.netty.util.AttributeKey;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        String host = "localhost";//设置目标服务器的主机地址为本地服务器 (localhost)。
        int port = 8080;//定义目标服务器的端口号为 8080。
        EventLoopGroup workerGroup = new NioEventLoopGroup();//创建一个工作线程组，负责处理 I/O 操作。
        // NioEventLoopGroup 是 Netty 提供的线程池实现，基于 Java NIO。
        try {
            Bootstrap b = new Bootstrap(); //初始化一个 Bootstrap 实例，用于配置客户端。
            b.group(workerGroup); //将工作线程组绑定到 Bootstrap。
            b.channel(NioSocketChannel.class); //端使用的通道类型为 NioSocketChannel，即基于 NIO 的客户端通道。
            b.option(ChannelOption.SO_KEEPALIVE, true); //配置通道选项，启用 TCP 的 SO_KEEPALIVE，保持连接活跃。
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
            System.out.println("Client started");
            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); //连接到服务器并等待连接完成 (sync() 会阻塞直到完成)。
            f.channel().writeAndFlush("hello server\r\n").sync();//向服务器发送字符串消息 "hello server"。
            //添加分隔符，标志消息结束（与 DelimiterBasedFrameDecoder 配合使用）。
            f.channel().closeFuture().sync();//阻塞当前线程，直到通道关闭。
            Object result = f.channel().attr(AttributeKey.valueOf("sssss")).get();//获取通道的自定义属性 sssss 的值，通常由服务端返回的数据设置。
            System.out.println("获取到服务器返回的数据"+result.toString());//打印服务器返回的数据。
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
