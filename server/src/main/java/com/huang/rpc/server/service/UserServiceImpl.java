package com.huang.rpc.server.service;

import com.huang.rpc.api.service.UserService;
import com.huang.rpc.server.annotation.Protocol;
import com.huang.rpc.server.annotation.Version;

public class UserServiceImpl implements UserService {

    @Override
    @Version("v1.0")
    @Protocol("http")
    public String say(String param) {
        return "Netty Server 版本1.0实现" + param;
    }
    
}
