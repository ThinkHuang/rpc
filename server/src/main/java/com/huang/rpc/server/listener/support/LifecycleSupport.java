package com.huang.rpc.server.listener.support;

import com.huang.rpc.server.listener.Lifecycle;
import com.huang.rpc.server.listener.LifecycleEvent;
import com.huang.rpc.server.listener.LifecycleListener;

/**
 * 采用facade模式，对外只暴露合适的方法
 */
public class LifecycleSupport implements Lifecycle {
    
    private Lifecycle lifecycle;
    
    private LifecycleListener listeners[] = new LifecycleListener[0];

    public LifecycleSupport(Lifecycle lifecycle) {
        super();
        this.lifecycle = lifecycle;
    }

    @Override
    public void fireEvent(LifecycleEvent event) {
        LifecycleListener interested[] = listeners;
        for (int i = 0; i < interested.length; i++)
            interested[i].lifecycleEvent(event);
    }
    
}
