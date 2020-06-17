package com.huang.rpc.server.listener;


import com.huang.rpc.server.listener.support.LifecycleEvent;
import com.huang.rpc.server.listener.support.LifecycleSupport;

public abstract class AbstractLifecycle implements Lifecycle {
    
    private LifecycleSupport lifecycle = new LifecycleSupport(this);

    @Override
    public void fireEvent(LifecycleEvent event) {
        lifecycle.fireEvent(event);
    }
}
