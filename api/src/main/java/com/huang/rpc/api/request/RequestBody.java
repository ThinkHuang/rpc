package com.huang.rpc.api.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class RequestBody implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = -5374628363695704734L;

    /**
     * 类名
     */
    private String className;
    
    /**
     * 方法名
     */
    private String methodName;
    
    /**
     * 参数类型列表
     */
    private Class<?>[] paramTypes;
    
    /**
     * 参数值列表
     */
    private Object[] paramValues;
}
