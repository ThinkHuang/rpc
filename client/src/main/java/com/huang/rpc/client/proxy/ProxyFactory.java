package com.huang.rpc.client.proxy;

public interface ProxyFactory
{
    public <T> T createProxy(final Class<?> target);
}
