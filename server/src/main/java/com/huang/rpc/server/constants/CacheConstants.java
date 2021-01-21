package com.huang.rpc.server.constants;

/**
 * 缓存常量
 * @author huangyejun
 *
 */
public interface CacheConstants {
    
    /**
     * 区域
     */
    interface region {
        /**
         * 服务实例
         */
        String SERVICE_INSTANCE = "service_instance";
        
        /**
         * 服务名称
         */
        String SERVICE_NAME = "service_name";
    }
}
