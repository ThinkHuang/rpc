package com.huang.rpc.server.listener.support;

import com.huang.rpc.server.listener.Lifecycle;
import com.huang.rpc.server.listener.LifecycleListener;

/**
 * 采用facade模式，对外只暴露合适的方法
 * 现对LifecycleSupport用法进行说明，必须实现下述两个动作
 *  1、需要进行事件通知的对象都必须实现和LifecycleSupport相同的接口（Lifecycle）
 *  2、所有事件通知的对象都存在LifecycleSupport对象的引用，然后通过LifecycleSupport完成事件的通知调用和其他生命周期函数。
 */
public class LifecycleSupport implements Lifecycle {
    
    private LifecycleListener[] listeners = new LifecycleListener[0];
    
    private final Object listenersLock = new Object(); // Lock object for changes to listeners

    @Override
    public void fireEvent(LifecycleEvent event) {
        LifecycleListener[] interested = listeners;
        for (int i = 0; i < interested.length; i++)
            interested[i].lifecycleEvent(event);
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener)
    {
        synchronized (listenersLock) {
            LifecycleListener results[] = new LifecycleListener[listeners.length + 1];
            for (int i = 0; i < listeners.length; i++)
                results[i] = listeners[i];
            results[listeners.length] = listener;
            listeners = results;
        }
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener)
    {
        synchronized (listenersLock) {
            int n = -1;
            for (int i = 0; i < listeners.length; i++) {
                if (listeners[i] == listener) {
                    n = i;
                    break;
                }
            }
            if (n < 0)
                return;
            LifecycleListener results[] =
              new LifecycleListener[listeners.length - 1];
            int j = 0;
            for (int i = 0; i < listeners.length; i++) {
                if (i != n)
                    results[j++] = listeners[i];
            }
            listeners = results;
        }
    }
    
}
