package com.huang.rpc.server.service;

import com.huang.rpc.api.service.UserService;
import com.huang.rpc.server.annotation.Protocol;
import com.huang.rpc.server.annotation.Version;

public class UserServiceImpl1 implements UserService {
    
    @Override
    @Version("v1.1")
    @Protocol("https")
    public String say(String param) {
        return "Netty Server 版本1.1实现 " + param;
    }
    
}
