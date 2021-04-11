package com.huang.rpc.server.registry.support.redis;

import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.server.handler.Invocation;
import com.huang.rpc.server.registry.AbstractServiceRegistry;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2Cache;

public class RedisServiceRegistry extends AbstractServiceRegistry
{
    private static final Logger log = LoggerFactory.getLogger(RedisServiceRegistry.class);
    
    private static CacheChannel cache;
    
    public RedisServiceRegistry() {
        cache = J2Cache.getChannel();
    }
    
    @Override
    public void doPublish(String basePackage)
    {
        
    }
    
    @Override
    public void registr(URL url)
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
