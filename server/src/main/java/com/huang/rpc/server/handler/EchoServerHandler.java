package com.huang.rpc.server.handler;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.server.registry.support.ConcurrentServiceRegistry;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

@Sharable
public class EchoServerHandler extends ChannelHandlerAdapter {
    
    /**
     * When the channel received the message from Inbound, this method was invoked
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // client发送过来的请求实体
        RequestBody body = (RequestBody)msg;
        Invocation invocation = ConcurrentServiceRegistry.getInvocation(body);
        if (null != invocation) {
            Object result = invocation.invoke();
            ctx.writeAndFlush(result);
        }
        System.out.println("服务端手动请求");
    }
    
    /**
     * when the message was the last block,this method was invoked
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
    
    /**
     * when exception occurred, this method was invoked
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    
}
