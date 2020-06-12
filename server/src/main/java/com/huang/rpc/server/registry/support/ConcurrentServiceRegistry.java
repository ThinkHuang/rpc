package com.huang.rpc.server.registry.support;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.server.annotation.Version;
import com.huang.rpc.server.config.GlobalConfig;
import com.huang.rpc.server.constants.ExceptionConstants;
import com.huang.rpc.server.constants.LoaderConstants;
import com.huang.rpc.server.exception.RpcException;
import com.huang.rpc.server.handler.Invocation;
import com.huang.rpc.server.handler.Invoker;
import com.huang.rpc.server.handler.RpcInvocation;
import com.huang.rpc.server.init.Loader;
import com.huang.rpc.server.init.support.RpcLoader;
import com.huang.rpc.server.registry.AbstractServiceRegistry;

import javax.validation.constraints.NotNull;

/**
 * 对放到指定目录下的service服务
 * @author Administrator
 *
 */
public class ConcurrentServiceRegistry extends AbstractServiceRegistry {
    
    private static final Logger log = LoggerFactory.getLogger(ConcurrentServiceRegistry.class);
    
    // 接口名和服务名的映射关系缓存(这里服务开始支持多服务)
    private static final Map<Integer, List<String>> serviceNameCache = new ConcurrentHashMap<>();
    
    // 实例池，所有的服务实例都会缓存在该对象中
    private static final Map<Integer, Object> serviceMapper = new ConcurrentHashMap<>();
    
    private static Loader loader = null;
    
    @NotNull
    /**
     * 该成员变量不能为空
     */
    private static CacheKey cacheKey;
    
    static
    {
        // 加载默认的配置文件rpc.properties，读取其中的配置文件，以key-value的形式存储，
        // TODO:后续考虑使用热更新技术来动态获取配置文件,需要使用后台线程定时读取文件的更新时间
        loader = new RpcLoader();
        loader.load(GlobalConfig.RPC_PROPERTIES);
    }
    
    @Override
    public void doPublish(String basePackage) {
        // TODO:为后面服务注册提供基础
        // 服务注册
        // register();
    }
    
    
    
    @Override
    public void registr(URL url) {
    }
    
    /**
     * 通过参数获取调用器
     * @param body 目前为止是具体类型，后面需要改为执行类型
     * @return
     * @throws ReflectiveOperationException
     */
    public static Invoker getInvoker(RequestBody body) throws ReflectiveOperationException {
        return null;
    }
    
    /**
     * 获取调用点
     * @param body
     * @return
     * @throws ReflectiveOperationException
     */
    public static Invocation getInvocation(RequestBody body) throws ReflectiveOperationException {
        String className = body.getClassName();
        // 找到服务名
        if (log.isInfoEnabled()) {
            log.info("{} ? singleton : prototype", loader.getPropertyMap().get(LoaderConstants.RPC_SERVICE_SINGLETON));
        }
         String version = body.getVersion();
        // String protocol = body.getProtocol();
        boolean singleton = Objects.equals("true", loader.getPropertyMap().get(LoaderConstants.RPC_SERVICE_SINGLETON));
        // TODO：这里不仅仅只能根据className进行缓存，还得考虑和version和protocol进行hash
        // 目前对className和version进行hash，后续加入了protocol后，将会一起进行hash
        cacheKey = new CacheKey(className, version);
        if (singleton && serviceMapper.containsKey(getCacheKey())) {
            return (Invocation)serviceMapper.get(getCacheKey());
        } else {
            // 如果全限定名服务不存在，那么直接返回空
            List<String> serviceUniqueNames = getServiceName(className, singleton);
            if (null == serviceUniqueNames) {
                throw new RpcException(ExceptionConstants.UNKNOWN_EXCEPTION);
            }
            return getCertainInvocation(serviceUniqueNames, version, body);
        }
    }
    
    /**
     * 获取具体的服务实例
     * @param serviceUniqueNames
     * @param versionName
     * @param body
     */
    private static Invocation getCertainInvocation(final List<String> serviceUniqueNames, final String versionName, final RequestBody body)
            throws ReflectiveOperationException {
        // TODO:这里暂时做的是用到即初始化
        for (String serviceName : serviceUniqueNames) {
            Object clazz = Class.forName(serviceName).newInstance();
            Method method = clazz.getClass().getMethod(body.getMethodName(), body.getParamTypes());
            if (method.isAnnotationPresent(Version.class)) {
                Version version = method.getAnnotation(Version.class);
                if (version.value().equals(versionName)) {
                    Invocation invocation = new RpcInvocation(clazz, method, body.getParamValues());
                    serviceMapper.put(getCacheKey(), invocation);
                    return invocation;
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
    private static synchronized List<String> getServiceName(final String className, boolean singleton) {
        if (getServiceCache().isEmpty()) {
            return null;
        }
        if (singleton && serviceNameCache.containsKey(getCacheKey())) {
            return serviceNameCache.get(getCacheKey());
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
                if (!serviceNames.isEmpty()) {
                    // 在加载的时候会采用覆盖模式，一旦服务满足，后续服务会覆盖掉先前注册的服务，
                    // TODO：但是一旦服务启动，不再会重新覆盖，新的服务实例不会生效，这是一个问题，不能对服务的更新做相应，这里可以考虑做服务监听
                    // 现在开始返回一个接口的多个实现实例
                    serviceNameCache.put(getCacheKey(), new ArrayList<>(serviceNames));
                } else {
                    // do nothing no instants return
                }
            } catch (ClassNotFoundException e) {
                log.error("no service found:{}", e.getMessage(), e);
            }
        }
        return new ArrayList<>(serviceNames);
    }
    
    protected static int getCacheKey() {
        return cacheKey.hashCode();
    }
    
    /**
     * 缓存键生成
     *
     */
    static final class CacheKey {
        
        /**
         * 接口类名称
         */
        private String className;
        
        /**
         * 版本实例
         */
        private String version;
        
        public CacheKey(String className, String version) {
            this.className = className;
            this.version = version;
        }

        @Override
        public final int hashCode() {
            return className.hashCode() ^ version.hashCode();
        }

    }
    
}
