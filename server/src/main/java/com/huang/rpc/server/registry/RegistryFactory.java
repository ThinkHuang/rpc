package com.huang.rpc.server.registry;

import java.util.Map;

import com.huang.rpc.server.config.GlobalConfig;
import com.huang.rpc.server.constants.ExceptionConstants;
import com.huang.rpc.server.constants.LoaderConstants;
import com.huang.rpc.server.exception.RpcException;
import com.huang.rpc.server.init.Loader;
import com.huang.rpc.server.init.support.RpcLoader;
import com.huang.rpc.server.registry.support.ConcurrentServiceRegistry;
import com.huang.rpc.server.registry.support.RedisServiceRegistry;
import com.huang.rpc.server.registry.support.ZookeeperServiceRegistry;

import cn.hutool.core.util.ObjectUtil;

/**
 * 通过工厂类实例化registry的具体实现
 * @author huangyejun
 *
 */
public class RegistryFactory
{
    
    private static final Registry REGISTRY;
    
    private static final Loader LOADER;
    
    private RegistryFactory() {}
    
    static {
        // 加载默认的配置文件rpc.properties，读取其中的配置文件，以key-value的形式存储
        // TODO:后续考虑使用热更新技术来动态获取配置文件,需要使用后台线程定时读取文件的更新时间
        LOADER = new RpcLoader();
        LOADER.load(GlobalConfig.RPC_PROPERTIES);
        Map<String, String> properties = LOADER.getPropertyMap();
        String rpcServiceRegistry = properties.get(LoaderConstants.RPC_SERVICE_REGISTRY);
        if (ObjectUtil.isEmpty(rpcServiceRegistry) || GlobalConfig.Registry.CONCURRENT.equals(rpcServiceRegistry)) {
            REGISTRY = new ConcurrentServiceRegistry();
        } else if(GlobalConfig.Registry.REDIS.equals(rpcServiceRegistry)) {
            REGISTRY = new RedisServiceRegistry();
        } else if(GlobalConfig.Registry.ZOOKEEPER.equals(rpcServiceRegistry)) {
            REGISTRY = new ZookeeperServiceRegistry();
        } else {
            throw new RpcException(ExceptionConstants.NO_SERVICE_REGISTRY_FOUND_EXCEPTION);
        }
    }
    
    /**
     * 获取注册器
     * @return
     */
    public static Registry getRegistry() {
        return REGISTRY;
    }
    
    /**
     * 获取加载器
     * @return
     */
    public static Loader getLoader() {
        return LOADER;
    }
}
