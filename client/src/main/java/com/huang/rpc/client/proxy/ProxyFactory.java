package com.huang.rpc.client.proxy;

public interface ProxyFactory
{
    
    /**
     * 不基于版本和协议控制
     * @param target
     * @return
     */
    public <T> T createProxy(final Class<?> target);
    /**
     * 基于版本控制和协议控制
     * @param target
     * @param version
     * @param protocol 
     * @return
     */
    public <T> T createProxy(final Class<?> target, String version, String protocol);
}
