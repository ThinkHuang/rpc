package com.huang.rpc.server.spi;

import com.huang.rpc.api.service.UserService;
import com.huang.rpc.server.annotation.Protocol;
import com.huang.rpc.server.annotation.Version;

public class UserServiceSPIImpl implements UserService
{
    @Override
    @Version("v2.0")
    @Protocol("http")
    public String say(String param)
    {
        return "Netty Server using SPI strategy implement Service registration " + param;
    }
}
