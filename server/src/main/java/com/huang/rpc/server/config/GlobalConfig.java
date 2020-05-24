package com.huang.rpc.server.config;

public interface GlobalConfig
{
    int SERVER_PORT = 8088;
    
    /**
     * 现在采用这种hardcode写法，之后要试着采用SPI的形式提供接口
     */
    String BASE_PACKAGE = "com.huang.rpc.server.service";
}
