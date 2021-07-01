package com.huang.rpc.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.server.constants.Result;
import com.huang.rpc.server.registry.Registry;
import com.huang.rpc.server.registry.RegistryFactory;

import cn.hutool.core.util.ObjectUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

@Sharable
public class EchoServerHandler extends ChannelHandlerAdapter {
    
    private static final Logger log = LoggerFactory.getLogger(EchoServerHandler.class);
    /**
     * When the channel received the message from Inbound, this method was invoked
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // client发送过来的请求实体
        RequestBody body = (RequestBody)msg;
        Registry registry = RegistryFactory.getRegistry();
        Invocation invocation = registry.getInvocation(body);
        if (log.isInfoEnabled()) {
            log.info("当前获取到的调用实例为：{}", invocation);
        }
        if (ObjectUtil.isNotEmpty(invocation)) {
            Invoker invoker = new RpcInvoker();
            Result result = invoker.invoke(invocation);
            ctx.writeAndFlush(result.getData());
        } else {
            log.info("未找到可用实例，请确认传参是否正确!");
        }
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
