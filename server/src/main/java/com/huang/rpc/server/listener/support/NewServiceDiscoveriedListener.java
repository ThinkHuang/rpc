package com.huang.rpc.server.listener.support;

import com.huang.rpc.server.listener.LifecycleListener;

public class NewServiceDiscoveriedListener implements LifecycleListener
{
    
    @Override
    public void lifecycleEvent(LifecycleEvent event)
    {
        System.out.println("我是一个新的服务实例，请知悉");
    }
    
}
