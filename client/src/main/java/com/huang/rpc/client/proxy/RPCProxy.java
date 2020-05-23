package com.huang.rpc.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.client.handler.EchoClientHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class RPCProxy {
    
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(final Class<?> target) {
        return (T)Proxy.newProxyInstance(target.getClassLoader(), 
                                        new Class[]{target}, 
                                        new InvocationHandler() {
            
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RequestBody body = new RequestBody();
                body.setClassName(target.getName());
                // 如果是Object的方法，那么直接返回
                if (method.getClass().isAssignableFrom(Object.class)) {
                    return method.invoke(this, args);
                }
                body.setMethodName(method.getName());
                body.setParamTypes(method.getParameterTypes());
                body.setParamValues(args);
                return send(body);
            }

            private Object send(RequestBody body) throws InterruptedException {
                final EchoClientHandler clientHandler = new EchoClientHandler();
                EventLoopGroup group = new NioEventLoopGroup();
                Bootstrap boot = new Bootstrap();
                try {
                    boot.group(group)
                        .channel(NioSocketChannel.class)
                        // 添加心跳服务
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new ChannelInitializer<Channel>() {
                            @Override
                            protected void initChannel(Channel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new ObjectEncoder());
                                pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                                pipeline.addLast(clientHandler);
                            }
                        
                        });
                    
                    ChannelFuture future = boot.connect("localhost", 8088).sync();
                    future.channel().writeAndFlush(body);
                    future.channel().closeFuture().sync();
                } finally {
                    group.shutdownGracefully().sync();
                }
                return clientHandler.getResponse();
            }
        });
    }
}
