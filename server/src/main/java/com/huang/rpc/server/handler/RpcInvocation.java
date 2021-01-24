package com.huang.rpc.server.handler;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcInvocation implements Invocation, Serializable {
    
    private static final Logger log = LoggerFactory.getLogger(RpcInvocation.class);
    
    /**
     * 
     */
    private static final long serialVersionUID = -5340731428920747697L;

    private transient Object clazz;
    
    private transient Method method;
    
    private transient Object[] arguments;
    
    public RpcInvocation(Object clazz, Method method) {
        this(clazz, method, null);
    }
    
    public RpcInvocation(Object clazz, Method method, Object[] arguments) {
        super();
        this.clazz = clazz;
        this.method = method;
        this.arguments = arguments;
    }
    
    public Object getClazz() {
        return clazz;
    }
    
    public void setClazz(Object clazz) {
        this.clazz = clazz;
    }
    
    public Method getMethod() {
        return method;
    }
    
    public void setMethod(Method method) {
        this.method = method;
    }
    
    public Object[] getArguments() {
        return arguments;
    }
    
    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
    
    @Override
    public Object invoke() {
        try {
            return method.invoke(clazz, arguments);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.error("远程方法调用异常：{}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String toString() {
        return "RpcInvocation [clazz=" + clazz + ", method=" + method + ", arguments=" + Arrays.toString(arguments) + "]";
    }
    
}
