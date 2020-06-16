package com.huang.rpc.server.registry;

import com.huang.rpc.server.registry.support.ConcurrentServiceRegistry;

/**
 * 通过工厂类实例化registry的具体实现
 * @author huangyejun
 *
 */
public class RegistryFactory
{
    
    
    private static Registry registry;
    
    static {
        // TODO:目前默认实例化ConcurrentServiceRegistry，后续改到rpc.properties去动态配置
        registry = new ConcurrentServiceRegistry();
    }
    
    /**
     * 获取注册器
     * @return
     */
    public static Registry getRegistry() {
        return registry;
    }
}
