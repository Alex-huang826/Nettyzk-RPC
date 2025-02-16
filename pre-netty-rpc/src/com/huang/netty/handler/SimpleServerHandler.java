package com.huang.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.huang.netty.handler.param.ServerRequest;
import com.huang.netty.util.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


public class SimpleServerHandler extends ChannelInboundHandlerAdapter {//Netty 提供的默认实现类，用于处理入站事件（如接收数据），可以通过重写方法来实现自定义行为。
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{//当服务器接收到客户端发送的消息时会触发。
        //ChannelHandlerContext ctx 管理当前通道上下文，提供对管道和通道的操作支持。
        //Object msg 收到的客户端消息，经过前面的解码器处理后通常是字符串。
        //System.out.println("Server received: " + msg.toString());
        //ctx.channel().writeAndFlush("is ok \r\n");//通过管道将消息返回给客户端。
        ServerRequest request = JSONObject.parseObject(msg.toString(),ServerRequest.class);
        //msg 是从客户端接收到的原始消息，通常是一个 JSON 格式的字符串。
        //JSONObject.parseObject() 是 FastJSON 提供的一个方法，用于将 JSON 字符串反序列化为 Java 对象。
        //ServerRequest.class 指定了目标类型，这样解析后的对象可以直接作为 ServerRequest 使用。
        //ServerRequest 类必须包含与 JSON 字符串中字段名称一致的属性，并提供对应的 getter 和 setter 方法。
        Response resp = new Response();//创建一个 Response 对象，用于构造发送给客户端的响应数据。
        resp.setId(request.getId());//从 request 对象中获取 id 值，并将其设置到响应对象 resp 中。
        resp.setResult("is okkkkkkkkk");//为响应对象设置结果内容。
        ctx.channel().writeAndFlush(JSONObject.toJSONString(resp));//将 Response 对象序列化为 JSON 字符串，并通过 Netty 的通道（Channel）发送回客户端。
        ctx.channel().writeAndFlush("\r\n");
        //{"id": "123", "result": "is okkkkkkkkk"}\r\n例子
        //接收并解析请求：从客户端接收 JSON 格式的请求消息，并将其转换为 Java 对象 ServerRequest。
        //生成响应：根据请求中的 id，生成一个带有 id 和固定响应结果的 Response 对象。
        //发送响应：将响应对象序列化为 JSON 字符串，并通过 Netty 的 Channel 发送回客户端。添加分隔符以标记消息结束。
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){//检查事件对象 evt 是否为 IdleStateEvent 类型。
            //IdleStateEvent 是 Netty 提供的一个事件类型，用于表示空闲状态（读、写或读写空闲）。
            IdleStateEvent event = (IdleStateEvent)evt;//将事件对象 evt 强制转换为 IdleStateEvent，以便访问其 state() 方法。
            if (event.state().equals(IdleState.READER_IDLE)){
                //如果当前通道在指定的读空闲时间内没有接收到数据（即 READER_IDLE 状态），触发该逻辑。
                System.out.println("读空闲=====");
                ctx.channel().close();
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                //如果当前通道在指定的写空闲时间内没有发送数据（即 WRITER_IDLE 状态），触发该逻辑。
                System.out.println("写空闲=====");
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                //如果当前通道在指定的读写空闲时间内既没有接收数据也没有发送数据（即 ALL_IDLE 状态），触发该逻辑。
                System.out.println("读写空闲=====");
                ctx.channel().writeAndFlush("ping\r\n");
                //在 20 秒内未发送或接收到任何数据，触发 IdleState.ALL_IDLE。发送 ping，导致 IdleState.WRITER_IDLE 和 IdleState.ALL_IDLE 被重置。
                //客户端没有发送有效响应，服务器在下一个 20 秒后再次触发 IdleState.ALL_IDLE，继续发送 ping。
                //形成循环：发送 ping → 触发 IdleState.ALL_IDLE → 再次发送 ping。
                //解决方法：为心跳操作添加计数限制。如果心跳次数达到一定阈值而未收到客户端响应，关闭连接。
                //要求客户端接收到 ping 后发送 pong 作为响应，服务器根据是否收到 pong 决定是否关闭连接
            }
        }
    }

}
