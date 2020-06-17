package com.huang.rpc.server.listener;

import com.huang.rpc.server.listener.support.LifecycleEvent;

/**
 * 暂时只充当一个标识性接口，一旦实现该接口说明其实现者需要实现生命周期方法
 * @author huangyejun
 *
 */
public interface LifecycleListener {

    /**
     * 执行生命周期方法
     * @param event
     */
    void lifecycleEvent(LifecycleEvent event);
    
}
