package com.huang.rpc.client;

import com.huang.rpc.api.service.UserService;
import com.huang.rpc.client.proxy.RPCProxy;

public class EchoClient {
    
    public static void main(String[] args) {
        UserService proxy = RPCProxy.createProxy(UserService.class);
        System.out.print(proxy.say("Hello World"));
    }
}
