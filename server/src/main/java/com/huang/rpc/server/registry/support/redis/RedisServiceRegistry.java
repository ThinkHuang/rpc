package com.huang.rpc.server.registry.support.redis;

import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.server.handler.Invocation;
import com.huang.rpc.server.registry.AbstractServiceRegistry;
import com.huang.rpc.server.utils.RedisUtils;

import cn.hutool.core.util.ObjectUtil;

public class RedisServiceRegistry extends AbstractServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(RedisServiceRegistry.class);
    
    protected static final String CACHE_PREFIX = "REDIS:";

    @Override
    public void doPublish(String basePackage) {

    }

    @Override
    public void register(URL url) {

    }

    @Override
    protected Invocation doGetInvocation(RequestBody body) throws ReflectiveOperationException {
        String className = body.getClassName();
        // 找到服务名
        if (logger.isInfoEnabled()) {
            logger.info("调用的缓存key为：{}", getCacheKey());
        }
        Invocation cachedInvocation = RedisUtils.getObject(getCacheKey());
        if (ObjectUtil.isNotEmpty(cachedInvocation)) {
            return cachedInvocation;
        } else {
            List<String> serviceUniqueNames = getServiceName(className);
            if (logger.isInfoEnabled()) {
                logger.info("服务实现接口为：{}", serviceUniqueNames);
            }
            Invocation invocation = getCertainInvocation(serviceUniqueNames, body);
            if (null != invocation) {
                RedisUtils.setObject(getCacheKey(), invocation);
            }
            return invocation;
        }
    }


    @Override
    protected synchronized String getCacheKey() {
        // 重写CacheKey的生成规则，redis需要加上region
        return CACHE_PREFIX + super.getCacheKey();
    }

    @Override
    public String toString() {
        return "RedisServiceRegistry";
    }

}
