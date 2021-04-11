package com.huang.rpc.server.registry;


import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.api.service.UserService;
import com.huang.rpc.server.annotation.Protocol;
import com.huang.rpc.server.annotation.Version;
import com.huang.rpc.server.constants.LoaderConstants;
import com.huang.rpc.server.handler.Invocation;
import com.huang.rpc.server.handler.RpcInvocation;
import com.huang.rpc.server.init.Loader;
import com.huang.rpc.server.listener.Lifecycle;
import com.huang.rpc.server.listener.LifecycleListener;
import com.huang.rpc.server.listener.support.LifecycleEvent;
import com.huang.rpc.server.listener.support.LifecycleSupport;
import com.huang.rpc.server.listener.support.NewServiceDiscoveriedListener;

public abstract class AbstractServiceRegistry implements Registry, Lifecycle {
    
    private static final Logger log = LoggerFactory.getLogger(AbstractServiceRegistry.class);
    
    private static final String CLASS_SUFFIX = ".class";

    /*************************************Static Area****************************************/
    // service全限定名称缓存
    private static final List<String> serviceCache = new CopyOnWriteArrayList<>();
    
    private static final Loader loader = RegistryFactory.getLoader();
    
    private static final LifecycleSupport lifecycle = new LifecycleSupport();
    
    private static final List<Invocation> fullServices = new ArrayList<>();
    
    /**************************************Member Variables**********************************/
    /**
     * 该成员变量不能为空
     */
    @NotNull
    private CacheKey cacheKey;
    
    /***********************************Public Method****************************************/
    @Override
    public void publish(String basePackage) {
        // 获取服务列表
        findServices(basePackage);
        // 使用Java的SPI机制进行服务注册
        findSPIServices(UserService.class);
        // 开始做服务发布
        doPublish(basePackage);
        // 获取所有已注册的服务实例列表
        findAllServices();
    }
    
    @Override
    public Invocation getInvocation(RequestBody body) throws ReflectiveOperationException
    {
        if (log.isInfoEnabled()) {
            log.info("请求体为：{}", body);
        }
        boolean singleton = Objects.equals("true", loader.getPropertyMap().get(LoaderConstants.RPC_SERVICE_SINGLETON));
        cacheKey = new CacheKey(body.getClassName(), body.getVersion(), body.getProtocol());
        return doGetInvocation(singleton, body);
    }
    
    
    
    @Override
    public void fireEvent(LifecycleEvent event)
    {
        // 使用委托机制调用LifecycleSupport的事件调用方法
        lifecycle.fireEvent(event);
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener)
    {
        // 使用委托机制调用LifecycleSupport添加监听器
        lifecycle.addLifecycleListener(listener);
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener)
    {
        // 使用委托机制调用LifecycleSupport删除监听器
        lifecycle.removeLifecycleListener(listener);
    }

    @Override
    public void registr(URL url)
    {
        
    }

    /***********************************Protected Method****************************************/
    /**
     * 获取具体的服务实例，如果走到该方法，说明在缓存中未找到合适的实例，这个时候就要触发事件监听
     * @param serviceUniqueNames
     * @param body
     */
    protected Invocation getCertainInvocation(final List<String> serviceUniqueNames, final RequestBody body) throws ReflectiveOperationException {
        // 这时会实例化所有的实现，可否考虑做懒加载的实现,容器的启动便是加载所有服务实例，无需做懒加载考虑
        // 这里暂时做的是用到即初始化
        final String versionName = body.getVersion();
        final String protocolName = body.getProtocol();
        Invocation invocation = null;
        for (String serviceName : serviceUniqueNames) {
            Object clazz = Class.forName(serviceName).newInstance();
            Method method = clazz.getClass().getMethod(body.getMethodName(), body.getParamTypes());
            // 提前实例化invocation，当存在符合条件的service时，直接返回，否则返回最新版本
            invocation = new RpcInvocation(clazz, method, body.getParamValues());
            if (method.isAnnotationPresent(Version.class) && method.isAnnotationPresent(Protocol.class)) {
                Version version = method.getAnnotation(Version.class);
                Protocol protocol = method.getAnnotation(Protocol.class);
                if (version.value().equalsIgnoreCase(versionName) && protocol.value().equalsIgnoreCase(protocolName)) {
                    // 触发监听事件执行，此时的事件监听者为服务实例容器，告知服务实例容器即可
                    fireEvent(new LifecycleEvent(this));
                    return invocation;
                }
            } 
            fullServices.add(invocation);
        }
        // 触发监听事件执行
        fireEvent(new LifecycleEvent(this));
        return invocation;
    }
    
    /**
     * 根据接口全限定名获取实现的服务的全限定名
     * @param className class名称
     * @return
     */
    protected synchronized List<String> getServiceName(final String className) {
        if (serviceCache.isEmpty()) {
            return null;
        }
        // TODO：基于dubbo的@SPI注解完成服务注册
        Set<String> serviceNames = new HashSet<>();
        // TODO:这里需要遍历所有的服务名称，是否考虑使用region的概念，来达到获取特定的目录的服务，同时也考虑做系统服务和自定义服务的隔离
        for (String serviceClassName : serviceCache) {
            try {
                Class<?> clazz = Class.forName(serviceClassName);
                Class<?>[] interfaces = clazz.getInterfaces();
                for (Class<?> interfaceClass : interfaces) {
                    // 找到了实现了接口的服务
                    if (className.equals(interfaceClass.getName())) {
                        serviceNames.add(serviceClassName);
                    }
                }
                // TODO：但是一旦服务启动，不再会重新覆盖，新的服务实例不会生效，这是一个问题，不能对服务的更新做响应，这里可以考虑做服务监听
                // 这里需要考虑的是新的服务实例是什么？新的服务实例将被xxxRegsitry发现，怎么发现的？如果搞不清楚这点将没法通过监听器监听到。
            } catch (ClassNotFoundException e) {
                log.error("no service found:{}", e.getMessage(), e);
            }
        }
        // 注册新的服务实例被发现事件
        addLifecycleListener(new NewServiceDiscoveriedListener());
        return new ArrayList<>(serviceNames);
    }
    
    /**
     * 获取缓存key
     * @return
     */
    protected synchronized int getCacheKey() {
        return cacheKey.getKey();
    }
    
    /***********************************Protected Method****************************************/
    protected abstract void doPublish(String basePackage);
    
    protected abstract Invocation doGetInvocation(boolean singleton, RequestBody body) throws ReflectiveOperationException;
    
    /***********************************Private Method****************************************/
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
                log.info("获取正常的实现类的全限定名，{}", filename);
                if (filename.endsWith(CLASS_SUFFIX)) {
                    String fQFileName = basePackage + "." + file.getName();
                    serviceCache.add(fQFileName.replace(CLASS_SUFFIX, ""));
                }
            }
        }
    }
    
    /**
     * 基于java SPI机制的服务发现
     * @param <T>
     */
    private <T> void findSPIServices(Class<T> clazz)
    {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
        final List<T> services = StreamSupport.stream(serviceLoader.spliterator(), false).collect(Collectors.toList());
        if (null != services) {
            for (T service : services) {
                // 获取到使用SPI注册的服务的全限定名
                if (log.isInfoEnabled()) {
                    log.info("使用SPI注册服务发现的服务全限定名称为：{}", service.getClass().getCanonicalName());
                }
                serviceCache.add(service.getClass().getCanonicalName());
            }
        }
    }

    /**
     * 获取所有的服务实例列表
     * @return
     */
    private List<Invocation> findAllServices() {
        if (!fullServices.isEmpty()) {
           for (Invocation invocation : fullServices) {
               if (log.isInfoEnabled()) {
                   log.info("服务实例为：{}", invocation);
               }
           }
        }
        return fullServices;
    }
    
    /**
     * 缓存键生成
     *
     */
    protected final class CacheKey {
        
        /**
         * 接口类名称
         */
        private String className;
        
        /**
         * 版本
         */
        private String version;
        
        /**
         * 协议
         */
        private String protocol;
        
        /**
         * 缓存 
         */
        private int key;
        
        public CacheKey(String className) {
            this(className, null);
        }
        
        public CacheKey(String className, String version) {
            this.className = className;
            this.version = version;
        }
        
        public CacheKey(String className, String version, String protocol) {
            this(className, version);
            this.protocol = protocol;
            initHashCode();
        }
        

        @Override
        public int hashCode() {
            // 缓存算法存在问题，导致v1.0和v2.0的生成相同
            int hashcode = className.hashCode();
            if (null != protocol) {
                hashcode ^= protocol.hashCode() & 0xFFEF;
            } 
            if (null != version) {
                hashcode ^= version.hashCode() & 0x0001;
            }
            return hashcode;
        }
        
        private void initHashCode() {
            key = hashCode();
        }

        public int getKey()
        {
            return key;
        }
        
    }
}
