package com.huang.netty.init;

//该类是一个基于 Netty 的 TCP 服务器，负责初始化并启动服务器，监听端口 8080，接收并处理客户端的连接。
import com.huang.netty.constant.Contants;
import com.huang.netty.factory.ZookeeperFactory;
import com.huang.netty.handler.ServerHandler;
import com.huang.netty.handler.SimpleServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@Component
public class NettyInital implements ApplicationListener<ContextRefreshedEvent> {
    //是 Spring 提供的一个事件监听器接口。
    //用于监听指定类型的事件（ContextRefreshedEvent 表示容器刷新事件）。
    //事件触发时，Spring 会自动调用 onApplicationEvent 方法。
    public void start() throws InterruptedException {
        EventLoopGroup parentGroup = new NioEventLoopGroup();//接收客户端的连接请求，每次有连接到来，会将其分配给 childGroup。
        EventLoopGroup childGroup = new NioEventLoopGroup();//处理已经被接受的连接（读写操作、业务逻辑处理）
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();//Netty 的服务器启动类，用于配置和启动服务器。
            bootstrap.group(parentGroup,childGroup);//指定 BOSS 和 WORKER 线程组
            bootstrap.option(ChannelOption.SO_BACKLOG,128);
            //SO_BACKLOG 指的是服务器的连接队列的最大长度，用于处理客户端的 TCP 连接请求，超过这个多余的连接请求可能会被拒绝
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE,false);//禁用 TCP 的长连接保活机制
            //TCP 的长连接保活机制（Keep-Alive）是 TCP 协议的一种功能，用于检测长时间空闲的 TCP 连接是否仍然有效，从而避免因连接断开或对端不可达而导致资源浪费或数据丢失。
            bootstrap.channel(NioServerSocketChannel.class);//使用基于 NIO 的服务器通道实现，用于接收客户端连接，是Netty对Java NIO的封装。
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
                    ch.pipeline().addLast(new StringDecoder());//将接收到的字节流数据解码为字符串。
                    ch.pipeline().addLast(new IdleStateHandler(60,45,20, TimeUnit.SECONDS));
                    //置了 Netty 通道的空闲检测机制。通过合理设置 readerIdleTime、writerIdleTime 和 allIdleTime，可以高效地监控连接的健康状态，并根据业务需求实现心跳检测或超时关闭。
                    ch.pipeline().addLast(new ServerHandler());//自定义处理器，负责处理业务逻辑。
                    ch.pipeline().addLast(new StringEncoder());//将发送的字符串数据编码为字节流。
                }
            });
            int port = 8080;
            int weight = 2;
            ChannelFuture f = bootstrap.bind(8080).sync();//sync阻塞当前线程，直到绑定操作完成。
            System.out.println("Server started on port 8080");
            CuratorFramework clint = ZookeeperFactory.create();//ZookeeperFactory.create() 方法负责创建一个 Zookeeper 客户端实例。
            //返回值是一个 CuratorFramework 对象，用于与 Zookeeper 交互。
            InetAddress netAddress = InetAddress.getLocalHost();//使用 InetAddress.getLocalHost() 获取当前主机的 IP 地址。
            clint.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(Contants.SERVER_PATH+"/"+netAddress.getHostAddress()+"#"+port+"#"+weight+"#");
            f.channel().closeFuture().sync();
            //调用 CuratorFramework.create() 方法向 Zookeeper 注册一个节点。
            //withMode(CreateMode.EPHEMERAL) 指定节点为临时节点：特性：临时节点会在客户端断开连接时自动删除。
            //forPath() 指定节点路径：
            //Contants.SERVER_PATH 是预定义的根路径（例如 /netty/）。
            //netAddress.getHostAddress() 是服务器的 IP 地址，用作节点名称。
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            this.start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

