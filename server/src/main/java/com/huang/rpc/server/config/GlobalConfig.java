package com.huang.rpc.server.config;

public interface GlobalConfig
{
    int SERVER_PORT = 8088;
    
    /**
     * 现在采用这种hardcode写法，之后要试着采用SPI的形式提供接口
     */
    String BASE_PACKAGE = "com.huang.rpc.server.service";
    
    String RPC_PROPERTIES = "rpc.properties";
    
    /**
     * 注册器
     * @author huangyejun
     *
     */
    interface Registry {
        
        String REDIS = "redis";
        
        String ZOOKEEPER = "zookeeper";
        
        String CONCURRENT = "concurrent";
    }
}
