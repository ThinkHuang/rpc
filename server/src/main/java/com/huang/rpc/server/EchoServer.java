package com.huang.rpc.server;

import java.net.InetSocketAddress;

import com.huang.rpc.server.handler.EchoServerHandler;
import com.huang.rpc.server.registry.ServiceRegistry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class EchoServer {
    /**
     * 定义端口
     */
    private final Integer port;
    
    public EchoServer(Integer port) {
        this.port = port;
    }
    
    public static void main(String[] args) throws Exception {
        int port = 8088;
        new ServiceRegistry().publish("com.huang.rpc.server.service");
        new EchoServer(port).start();
    }
    
    private void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap boot = new ServerBootstrap();
        try {
            boot.group(group, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                        pipeline.addLast(serverHandler);
                    }
                });
            ChannelFuture future = boot.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
