package com.huang.rpc.server.registry;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.server.annotation.Protocol;
import com.huang.rpc.server.annotation.Version;
import com.huang.rpc.server.handler.Invocation;
import com.huang.rpc.server.handler.RpcInvocation;

public abstract class AbstractServiceRegistry implements Registry {

    // service全限定名称缓存
    private static final List<String> serviceCache = new CopyOnWriteArrayList<>();
    
    @Override
    public void publish(String basePackage) {
        // 获取服务列表
        findServices(basePackage);
        // 开始做服务发布
        doPublish(basePackage);
    }
    
    private void findServices(String basePackage) {
        if (null == basePackage || "".equals(basePackage)) {
            return;
        }
        // 将com.huang.rpc.server.service变为com/huang/rpc/server/service
        String path = basePackage.replaceAll("\\.", "/");
        // 获取资源路径
        URL url = this.getClass().getClassLoader().getResource(path);
        File directory = new File(url.getFile());
        for (File file : directory.listFiles()) {
            // 判断资源路径是文件还是目录
            if (file.isDirectory()) {
                findServices(basePackage + "." + file.getName());
            } else {
                // 如果是文件，那么需要将文件的全限定名称缓存到serviceCache中
                String filename = file.getName();
                if (filename.endsWith(".class")) {
                    String fQFileName = basePackage + "." + file.getName();
                    serviceCache.add(fQFileName.replace(".class", ""));
                }
            }
        }
    }

    @Override
    public abstract void registr(URL url);
    
    public abstract void doPublish(String basePackage);

    protected static List<String> getServiceCache() {
        return serviceCache;
    }

    @Override
    public Invocation getInvocation(RequestBody body) throws ReflectiveOperationException
    {
        return null;
    }
    
    /**
     * 获取具体的服务实例
     * @param serviceUniqueNames
     * @param body
     */
    protected Invocation getCertainInvocation(final List<String> serviceUniqueNames, final RequestBody body) throws ReflectiveOperationException {
        // TODO：这时会实例化所有的实现，可否考虑做懒加载的实现
        // 这里暂时做的是用到即初始化
        final String versionName = body.getVersion();
        final String protocolName = body.getProtocol();
        for (String serviceName : serviceUniqueNames) {
            Object clazz = Class.forName(serviceName).newInstance();
            Method method = clazz.getClass().getMethod(body.getMethodName(), body.getParamTypes());
            if (method.isAnnotationPresent(Version.class) && method.isAnnotationPresent(Protocol.class)) {
                Version version = method.getAnnotation(Version.class);
                Protocol protocol = method.getAnnotation(Protocol.class);
                if (version.value().equalsIgnoreCase(versionName) && protocol.value().equalsIgnoreCase(protocolName)) {
                    Invocation invocation = new RpcInvocation(clazz, method, body.getParamValues());
                    // serviceMapper.put(getCacheKey(), invocation);
                    return invocation;
                }
            } else {
                // 如果没有版本控制和协议控制，返回第一个找到的实例。
                Invocation invocation = new RpcInvocation(clazz, method, body.getParamValues());
                // serviceMapper.put(getCacheKey(), invocation);
                return invocation;
            }
        }
        return null;
    }
    
    
}
