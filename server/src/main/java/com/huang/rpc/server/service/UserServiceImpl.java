package com.huang.rpc.server.service;

import com.huang.rpc.api.service.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public String say(String param) {
        return "Netty Server " + param;
    }
}
