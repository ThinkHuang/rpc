package com.huang.rpc.server.handler;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

public class RpcInvocation implements Invocation, Serializable {
    
    private static final long serialVersionUID = -5340731428920747697L;

    private Object clazz;
    
    private Method method;
    
    private Object[] arguments;
    
    private Class<?>[] paramTypes;
    
    private Invoker invoker;
    
    public RpcInvocation(Object clazz, Method method) {
        this(clazz, method, null);
    }
    
    public RpcInvocation(Object clazz, Method method, Object[] arguments) {
        super();
        this.clazz = clazz;
        this.method = method;
        this.arguments = arguments;
    }
    
    @Override
    public String toString() {
        return "RpcInvocation [clazz=" + clazz + ", method=" + method + ", arguments=" + Arrays.toString(arguments) + "]";
    }
    
    @Override
    public Object getClazz() {
        return clazz;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    @Override
    public Invoker getInvoker() {
        return invoker;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }
    
}
