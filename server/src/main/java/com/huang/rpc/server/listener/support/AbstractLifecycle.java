package com.huang.rpc.server.listener.support;


import com.huang.rpc.server.listener.Lifecycle;
import com.huang.rpc.server.listener.LifecycleEvent;

public abstract class AbstractLifecycle implements Lifecycle {
    
    private LifecycleSupport lifecycle = new LifecycleSupport(this);

    @Override
    public void fireEvent(LifecycleEvent event) {
        lifecycle.fireEvent(event);
    }
}
