package com.huang.rpc.server.handler;

import java.io.Serializable;
import java.lang.reflect.Method;

public interface Invocation extends Serializable {
    
    /**
     * get method.
     *
     * @serial
     */
    Method getMethod();

    /**
     * get parameter types.
     *
     * @return parameter types.
     * @serial
     */
    Class<?>[] getParamTypes();
    
    /**
     * get proxy object
     * @return
     */
    Object getClazz();

    /**
     * get arguments.
     *
     * @return arguments.
     * @serial
     */
    Object[] getArguments();

    /**
     * get the invoker in current context.
     *
     * @return invoker.
     * @transient
     */
    Invoker getInvoker();
    
}
