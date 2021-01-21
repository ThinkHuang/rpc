package com.huang.rpc.server.listener;

import com.huang.rpc.server.listener.support.LifecycleEvent;

/**
 * 生命周期函数
 * @author huangyejun
 */
public interface Lifecycle {
    
    /**
     * 触发事件监听
     * @param event
     */
    void fireEvent(LifecycleEvent event);
    
    /**
     * 添加事件监听
     * @param listener
     */
    void addLifecycleListener(LifecycleListener listener);
    
    /**
     * 移出事件监听
     * @param listener
     */
    void removeLifecycleListener(LifecycleListener listener);
}
