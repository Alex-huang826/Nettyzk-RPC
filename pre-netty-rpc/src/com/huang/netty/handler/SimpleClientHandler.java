package com.huang.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.huang.netty.client.DefaultFuture;
import com.huang.netty.util.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if("ping".equals(msg.toString())){
            ctx.channel().writeAndFlush("ping\r\n");
            return;
            //检查消息内容是否为 "ping"。
            //逻辑：msg.toString() 将接收到的消息对象转换为字符串，"ping".equals() 用于安全比较字符串内容。
            //通过当前通道的上下文 ctx.channel() 发送 "ping\r\n" 作为响应。发送给服务器
        }
        //System.out.println(msg.toString());
        //ctx.channel().attr(AttributeKey.valueOf("sssss")).set(msg);
        //将从服务器接收到的消息 msg 存储到通道的自定义属性 sssss 中。
        Response response = JSONObject.parseObject(msg.toString(),Response.class);
        //将接收到的 msg 数据解析为 Response 对象，假定 msg 是 JSON 格式的字符串，这一行代码使用 FastJSON 将其反序列化为 Java 对象。
        DefaultFuture.receive(response);
        //将解析后的 Response 对象传递给 DefaultFuture 的 receive() 方法，可能用于异步任务的结果处理或通知。
        //ctx.channel().close();
    }
}
