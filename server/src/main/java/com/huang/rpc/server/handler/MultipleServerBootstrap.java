package com.huang.rpc.server.handler;

import java.net.InetSocketAddress;

import com.huang.rpc.server.config.GlobalConfig;
import com.huang.rpc.server.init.Loader;
import com.huang.rpc.server.init.support.RpcLoader;
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

public class MultipleServerBootstrap extends AbstractBootstrap
{
    
    static
    {
        // 加载默认的配置文件rpc.properties，读取其中的配置文件，以key-value的形式存储，
        // TODO:后续考虑使用热更新技术来动态获取配置文件,需要使用后台线程定时读取文件的更新时间
        Loader loader = new RpcLoader();
        loader.load(GlobalConfig.RPC_PROPERTIES);
    }
    
    /**
     * 在ServerBootstrap中进行服务发现和注册
     */
    public MultipleServerBootstrap()
    {
        new ServiceRegistry().publish(GlobalConfig.BASE_PACKAGE);
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
