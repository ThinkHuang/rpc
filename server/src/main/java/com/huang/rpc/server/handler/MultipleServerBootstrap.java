package com.huang.rpc.server.handler;

import java.net.InetSocketAddress;

import com.huang.rpc.server.config.GlobalConfig;
import com.huang.rpc.server.registry.Registry;
import com.huang.rpc.server.registry.RegistryFactory;

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

public class MultipleServerBootstrap extends AbstractBootstrap
{
    
    /**
     * 在ServerBootstrap中进行服务发现和注册
     */
    public MultipleServerBootstrap()
    {
        Registry registry = RegistryFactory.getRegistry();
        registry.publish(GlobalConfig.BASE_PACKAGE);
    }
    
    @Override
    public void start() throws InterruptedException
    {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap boot = new ServerBootstrap();
        try
        {
            boot.group(group, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .localAddress(new InetSocketAddress(GlobalConfig.SERVER_PORT))
                    .childHandler(new ChannelInitializer<Channel>()
                    {
                        @Override
                        protected void initChannel(Channel ch) throws Exception
                        {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(serverHandler);
                        }
                    });
            ChannelFuture future = boot.bind().sync();
            future.channel().closeFuture().sync();
        }
        finally
        {
            group.shutdownGracefully().sync();
        }
    }
}
