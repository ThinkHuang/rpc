package com.huang.rpc.server.listener.support;

import java.util.EventObject;

import com.huang.rpc.server.listener.Lifecycle;

/**
 * 定义基础事件属性及相关对象的封装
 * @author huangyejun
 *
 */
public final class LifecycleEvent extends EventObject {

    /**
     * 
     */
    private static final long serialVersionUID = 4533949727245044130L;
    
    private Lifecycle data;
    
    public LifecycleEvent(Lifecycle data) {
        super(data);
    }

    public Object getData() {
        return data;
    }

    public void setData(Lifecycle data) {
        this.data = data;
    }
    
}
