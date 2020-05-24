package com.huang.rpc.server.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.server.registry.ServiceRegistry;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

@Sharable
public class EchoServerHandler extends ChannelHandlerAdapter {
    
    // 服务名称和实例映射关系缓存
    // TODO:将serviceMapepr放在这里不合适，需要放到前面去，先暂时放在这里
    private static final Map<String, Object> serviceMapper = new HashMap<>();
    
    /**
     * When the channel received the message from Inbound, this method was invoked
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // client发送过来的请求实体
        System.out.println("服务端手动请求");
        RequestBody body = (RequestBody)msg;
        Object clazz = null;
        String className = body.getClassName();
        // 找到服务名
        
        if (serviceMapper.containsKey(className)) {
            clazz = serviceMapper.get(className);
        } else {
            clazz = Class.forName(ServiceRegistry.serviceCache.get(0)).newInstance();
            serviceMapper.put(className, clazz);
        }
        // 开始服务调用
        String methodName = body.getMethodName();
        Class<?>[] paramTypes = body.getParamTypes();
        Method method = clazz.getClass().getMethod(methodName, paramTypes);
        Object[] paramValues = body.getParamValues();
        Object result = method.invoke(clazz, paramValues);
        ctx.writeAndFlush(result);
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
