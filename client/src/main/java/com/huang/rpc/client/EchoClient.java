package com.huang.rpc.client;

import com.huang.rpc.api.service.UserService;
import com.huang.rpc.client.proxy.ProxyFactory;
import com.huang.rpc.client.proxy.RPCProxyFactory;

public class EchoClient
{
    public static void main(String[] args)
    {
        ProxyFactory factory = new RPCProxyFactory();
        UserService proxy = factory.createProxy(UserService.class);
        System.out.println(proxy.say("Hello World"));
    }
}
