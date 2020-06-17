package com.huang.rpc.server.registry;


import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.server.annotation.Protocol;
import com.huang.rpc.server.annotation.Version;
import com.huang.rpc.server.config.GlobalConfig;
import com.huang.rpc.server.constants.LoaderConstants;
import com.huang.rpc.server.handler.Invocation;
import com.huang.rpc.server.handler.RpcInvocation;
import com.huang.rpc.server.init.Loader;
import com.huang.rpc.server.init.support.RpcLoader;

public abstract class AbstractServiceRegistry implements Registry {
    
    private static final Logger log = LoggerFactory.getLogger(AbstractServiceRegistry.class);

    // service全限定名称缓存
    private static final List<String> serviceCache = new CopyOnWriteArrayList<>();
    
    private static Loader loader = null;
    
    static {
        // 加载默认的配置文件rpc.properties，读取其中的配置文件，以key-value的形式存储，
        // TODO:后续考虑使用热更新技术来动态获取配置文件,需要使用后台线程定时读取文件的更新时间
        loader = new RpcLoader();
        loader.load(GlobalConfig.RPC_PROPERTIES);
    }
    
    @Override
    public void publish(String basePackage) {
        // 获取服务列表
        findServices(basePackage);
        // 开始做服务发布
        doPublish(basePackage);
    }
    
    public abstract void doPublish(String basePackage);

    public Invocation getInvocation(RequestBody body) throws ReflectiveOperationException
    {
        if (log.isInfoEnabled()) {
            log.info("{} ? singleton : prototype", loader.getPropertyMap().get(LoaderConstants.RPC_SERVICE_SINGLETON));
        }
        boolean singleton = Objects.equals("true", loader.getPropertyMap().get(LoaderConstants.RPC_SERVICE_SINGLETON));
        return doGetInvocation(singleton, body);
    }
    
    protected abstract Invocation doGetInvocation(boolean singleton, RequestBody body) throws ReflectiveOperationException;
    
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
                    return new RpcInvocation(clazz, method, body.getParamValues());
                }
            } 
        }
        return null;
    }
    
    /**
     * 根据接口全限定名获取实现的服务的全限定名
     * @param className class名称
     * @param singleton 是否单例
     * @return
     */
    protected static synchronized List<String> getServiceName(final String className, boolean singleton) {
        if (getServiceCache().isEmpty()) {
            return null;
        }
        // TODO：目前该集合没有特别的用途，由于只支持单服务，后期支持SPI的多服务模式，该Set即可发挥作用
        Set<String> serviceNames = new HashSet<>();
        // TODO:这里需要遍历所有的服务名称，是否考虑使用region的概念，来达到获取特定的目录的服务，同时也考虑做系统服务和自定义服务的隔离
        for (String serviceClassName : getServiceCache()) {
            try {
                Class<?> clazz = Class.forName(serviceClassName);
                Class<?>[] interfaces = clazz.getInterfaces();
                for (Class<?> interfaceClass : interfaces) {
                    // 找到了实现了接口的服务
                    if (Objects.equals(className, interfaceClass.getName())) { // NOTE:Objects.equals方法是1.7才出的
                        serviceNames.add(serviceClassName);
                    }
                }
//                if (!serviceNames.isEmpty()) {
//                    // 在加载的时候会采用覆盖模式，一旦服务满足，后续服务会覆盖掉先前注册的服务，
//                    // TODO：但是一旦服务启动，不再会重新覆盖，新的服务实例不会生效，这是一个问题，不能对服务的更新做相应，这里可以考虑做服务监听
//                    // 现在开始返回一个接口的多个实现实例
//                } else {
//                    // do nothing no instants return
//                }
            } catch (ClassNotFoundException e) {
                log.error("no service found:{}", e.getMessage(), e);
            }
        }
        return new ArrayList<>(serviceNames);
    }
    
    protected static List<String> getServiceCache() {
        return serviceCache;
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
}
