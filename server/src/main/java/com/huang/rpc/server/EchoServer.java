package com.huang.rpc.server;

import com.huang.rpc.server.handler.MultipleServerBootstrap;

public class EchoServer
{
    
    public static void main(String[] args) throws InterruptedException
    {
        MultipleServerBootstrap bootstrap = new MultipleServerBootstrap();
        bootstrap.start();
    }
    
}
