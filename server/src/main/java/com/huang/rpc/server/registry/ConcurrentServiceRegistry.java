package com.huang.rpc.server.registry;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.server.config.GlobalConfig;
import com.huang.rpc.server.constants.ExceptionConstants;
import com.huang.rpc.server.constants.LoaderConstants;
import com.huang.rpc.server.exception.RpcException;
import com.huang.rpc.server.handler.Invocation;
import com.huang.rpc.server.handler.Invoker;
import com.huang.rpc.server.handler.RpcInvocation;
import com.huang.rpc.server.init.Loader;
import com.huang.rpc.server.init.support.RpcLoader;


/**
 * 对放到指定目录下的service服务
 * @author Administrator
 *
 */
public class ConcurrentServiceRegistry extends AbstractServiceRegistry {
    
    private static final Logger log = LoggerFactory.getLogger(ConcurrentServiceRegistry.class);
    
    // 接口名和服务名的映射关系缓存
    private static final Map<String, String> serviceNameCache = new ConcurrentHashMap<>();
    
    // 服务名称和实例映射关系缓存
    private static final Map<String, Object> serviceMapper = new ConcurrentHashMap<>();
    
    private static Loader loader = null;
    
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
        Object clazz = null;
        String className = body.getClassName();
        // 找到服务名
        if (log.isInfoEnabled()) {
            log.info("{} ? singleton : prototype", loader.getPropertyMap().get(LoaderConstants.RPC_SERVICE_SINGLETON));
        }
        // TODO:开始实现基于协议和版本控制，建议使用注解
        String version = body.getVersion();
        String protocol = body.getProtocol();
        boolean singleton = Objects.equals("true", loader.getPropertyMap().get(LoaderConstants.RPC_SERVICE_SINGLETON));
        // TODO：这里不仅仅只能根据className进行缓存，还得考虑和version和protocol进行hash
        if (singleton && serviceMapper.containsKey(className)) {
            clazz = serviceMapper.get(className);
        } else {
            // 如果全限定名服务不存在，那么直接返回空
            String serviceUniqueName = getServiceName(className, singleton);
            if (null == serviceUniqueName) {
                throw new RpcException(ExceptionConstants.UNKNOWN_EXCEPTION);
            }
            clazz = Class.forName(serviceUniqueName).newInstance();
            serviceMapper.put(className, clazz);
        }
        String methodName = body.getMethodName();
        Class<?>[] paramTypes = body.getParamTypes();
        Method method = clazz.getClass().getMethod(methodName, paramTypes);
        Object[] paramValues = body.getParamValues();
        return new RpcInvocation(clazz, method, paramValues);
    }
    
    /**
     * 根据接口全限定名获取实现的服务的全限定名
     * @param className class名称
     * @param singleton 是否单例
     * @return
     */
    private static synchronized String getServiceName(final String className, boolean singleton) {
        if (getServicecache().isEmpty()) {
            return null;
        }
        if (singleton && serviceNameCache.containsKey(className)) {
            return serviceNameCache.get(className);
        }
        // TODO：目前该集合没有特别的用途，由于只支持单服务，后期支持SPI的多服务模式，该Set即可发挥作用
        Set<String> serviceNames = new HashSet<>();
        // TODO:这里需要遍历所有的服务名称，是否考虑使用region的概念，来达到获取特定的目录的服务，同时也考虑做系统服务和自定义服务的隔离
        for (String serviceClassName : getServicecache()) {
            try {
                Class<?> clazz = Class.forName(serviceClassName);
                Class<?>[] interfaces = clazz.getInterfaces();
                if (log.isInfoEnabled()) {
                    log.info("create a new service ...");
                }
                for (Class<?> interfaceClass : interfaces) {
                    // 找到了实现了接口的服务
                    if (Objects.equals(className, interfaceClass.getName())) {
                        serviceNames.add(serviceClassName);
                    }
                }
                if (serviceNames.size() == 1) {
                    // TODO:依赖于JDK的版本，这里实现不优雅
                    Optional<String> optional = serviceNames.stream().findFirst();
                    if (optional.isPresent()) {
                        String targetServiceName = optional.get();
                        serviceNameCache.put(className, targetServiceName);
                        return targetServiceName;
                    }
                } else if (serviceNames.size() > 1) {
                    serviceNames.stream().forEach(serviceName -> serviceNameCache.put(className, serviceName));
                    // throw new RpcException(ExceptionConstants.MULTIPLE_SERVICE_EXCEPTION);
                } else {
                    // do nothing no instants return
                }
            } catch (ClassNotFoundException e) {
                log.error("no service found:{}", e.getMessage(), e);
            }
        }
        return null;
    }
    
    /**
     * Just for test provider
     * @return
     */
    public static List<String> getServicecache() {
        return getServicecache();
    }

}
