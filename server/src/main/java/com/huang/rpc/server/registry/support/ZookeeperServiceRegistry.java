package com.huang.rpc.server.registry.support;

import java.net.URL;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.server.handler.Invocation;
import com.huang.rpc.server.registry.AbstractServiceRegistry;

public class ZookeeperServiceRegistry extends AbstractServiceRegistry
{

    @Override
    public void doPublish(String basePackage)
    {
        
    }

    @Override
    public void register(URL url)
    {
        
    }

    @Override
    protected Invocation doGetInvocation(RequestBody body) throws ReflectiveOperationException {
        return null;
    }

    @Override
    public String toString()
    {
        return "ZookeeperServiceRegistry";
    }
    
}
