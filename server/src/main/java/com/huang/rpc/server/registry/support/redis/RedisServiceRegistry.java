package com.huang.rpc.server.registry.support.redis;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.server.handler.Invocation;
import com.huang.rpc.server.registry.AbstractServiceRegistry;

public class RedisServiceRegistry extends AbstractServiceRegistry
{
    private static final Logger logger = LoggerFactory.getLogger(RedisServiceRegistry.class);
    
    
    @Override
    public void doPublish(String basePackage)
    {
        
    }
    
    @Override
    public void register(URL url)
    {
        
    }

    @Override
    protected Invocation doGetInvocation(boolean singleton, RequestBody body) throws ReflectiveOperationException {
        return null;
    }

    @Override
    public String toString()
    {
        return "RedisServiceRegistry";
    }
    
    
}
