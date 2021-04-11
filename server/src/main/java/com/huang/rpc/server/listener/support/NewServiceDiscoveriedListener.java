package com.huang.rpc.server.listener.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huang.rpc.server.listener.LifecycleListener;
import com.huang.rpc.server.registry.Registry;

public class NewServiceDiscoveriedListener implements LifecycleListener
{
    
    private final static Logger logger = LoggerFactory.getLogger(NewServiceDiscoveriedListener.class);
    
    @Override
    public void lifecycleEvent(LifecycleEvent event)
    {
        Registry registry = (Registry)event.getSource();
        // 有新的服务实例被注册到该容器中去了
        logger.info("{}中注册了新的服务实例，请知悉", registry.toString());
    }
    
}
