package com.huang.rpc.server.handler;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huang.rpc.server.constants.Result;

public abstract class AbstractInvoker implements Invoker {

    private static final Logger log = LoggerFactory.getLogger(AbstractInvoker.class);

    @Override
    public Result invoke(Invocation invocation) {
        try {
            return new Result(invocation.getMethod().invoke(invocation.getClazz(), invocation.getArguments()));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.error("远程方法调用异常：{}", e.getMessage(), e);
        }
        return null;
    }

}
