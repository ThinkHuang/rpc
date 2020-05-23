package com.huang.rpc.client.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * marks this class as one whose instances can be shared among channels
 */
@Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<Object> {
    
    private Object response;
    
    
    public Object getResponse() {
        return response;
    }

    /**
     * on exception,logs the error and close channel
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.print("我报错了");
        cause.printStackTrace();
        ctx.close();
    }
    
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        response = msg;
    }
    
}
