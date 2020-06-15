package com.huang.rpc.server.listener;

/**
 * 生命周期函数
 * @author huangyejun
 */
public interface Lifecycle {
    
    void fireEvent(LifecycleEvent event);
}
