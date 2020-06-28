package com.huang.rpc.server.handler;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RpcInvocation implements Invocation, Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -5340731428920747697L;

    private Object clazz;
    
    private Method method;
    
    private Object[] arguments;
    
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
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
