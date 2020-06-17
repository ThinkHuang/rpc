package com.huang.rpc.server.listener;

import com.huang.rpc.server.listener.support.LifecycleEvent;

/**
 * 生命周期函数
 * @author huangyejun
 */
public interface Lifecycle {
    
    void fireEvent(LifecycleEvent event);
}
