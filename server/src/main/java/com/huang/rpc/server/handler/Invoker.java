package com.huang.rpc.server.handler;

import com.huang.rpc.server.constants.Result;

public interface Invoker {
    
    /**
     * 定义实际的远程调用方法
     * @param invocation
     * @return
     */
    Result invoke(Invocation invocation);
}
