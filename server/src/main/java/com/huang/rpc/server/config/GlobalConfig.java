package com.huang.rpc.server.config;

public interface GlobalConfig
{
    int SERVER_PORT = 8088;
    
    /**
     * 现在采用这种hardcode写法，之后要试着采用SPI的形式提供接口
     */
    String BASE_PACKAGE = "com.huang.rpc.server.service";
    
    interface ExceptionDir {
        int UNKNOWN_EXCEPTION = 0;
        int NETWORK_EXCEPTION = 1;
        int TIMEOUT_EXCEPTION = 2;
        int BIZ_EXCEPTION = 3;
        int FORBIDDEN_EXCEPTION = 4;
        int SERIALIZATION_EXCEPTION = 5;
    }
}
