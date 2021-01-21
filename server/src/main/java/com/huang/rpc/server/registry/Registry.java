package com.huang.rpc.server.registry;

import java.net.URL;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.server.handler.Invocation;

/**
 * 定义注册接口，所有的注册服务都需要实现改接口
 * @author huangyejun
 *
 */
public interface Registry
{
    /**
     * 服务发布
     * @param basePackage 注册的服务路径
     */
    void publish(String basePackage);
    
    /**
     * 注册的服务地址
     * @param url
     */
    void registr(URL url);
    
    /**
     * 获取调用器实例，根据请求body获取Invocation
     * @param body
     * @return
     */
    Invocation getInvocation(RequestBody body) throws ReflectiveOperationException;
    
}
