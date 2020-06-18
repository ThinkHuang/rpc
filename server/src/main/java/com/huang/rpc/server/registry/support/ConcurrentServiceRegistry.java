package com.huang.rpc.server.registry.support;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.server.handler.Invocation;
import com.huang.rpc.server.handler.Invoker;
import com.huang.rpc.server.registry.AbstractServiceRegistry;

/**
 * @author huangyejun
 *
 */
public class ConcurrentServiceRegistry extends AbstractServiceRegistry {
    
    private static final Logger log = LoggerFactory.getLogger(ConcurrentServiceRegistry.class);
    
    /**
     * TODO：基于服务实例可以只缓存实现者，但是，如果是做多版本控制和协议控制，那么就需要抽象到方法层面了。
     * 现在要将服务实现者和方法实例分开实现
     */
    // 接口名和服务名的映射关系缓存(这里服务开始支持多服务)
    private static final Map<Integer, List<String>> serviceNameCache = new ConcurrentHashMap<>();
    
    // 实例池，所有的服务实例都会缓存在该对象中
    private static final Map<Integer, Invocation> serviceMapper = new ConcurrentHashMap<>();
    
    @Override
    public void doPublish(String basePackage) {
        // TODO:为后面服务注册提供基础
        // 服务注册
        // register();
    }
    
    @Override
    public void registr(URL url) {
    }
    
    /**
     * 通过参数获取调用器
     * @param body 目前为止是具体类型，后面需要改为执行类型
     * @return
     */
    public static Invoker getInvoker(RequestBody body) {
        return null;
    }
    
    /**
     * 获取调用点
     * @param singleton
     * @param body
     * @return
     * @throws ReflectiveOperationException
     */
    @Override
    public Invocation doGetInvocation(boolean singleton, RequestBody body) throws ReflectiveOperationException {
        String className = body.getClassName();
        // 找到服务名
        if (singleton && serviceMapper.containsKey(getCacheKey())) {
            return serviceMapper.get(getCacheKey());
        } else {
            // 如果全限定名服务不存在，那么直接返回空--------------------*
            List<String> serviceUniqueNames = serviceNameCache.get(getCacheKey());
            if (null == serviceUniqueNames) {
                serviceUniqueNames = getServiceName(className);
            }
            serviceNameCache.put(getCacheKey(), serviceUniqueNames);
            if (log.isInfoEnabled()) {
                log.info("服务实现接口为：{}", serviceUniqueNames);
            }
            Invocation invocation = getCertainInvocation(serviceUniqueNames, body);
            if (null != invocation) {
                serviceMapper.put(getCacheKey(), invocation);
            }
            return invocation;
        }
    }
}
