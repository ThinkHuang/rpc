package com.huang.rpc.server.registry;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.server.handler.Invocation;
import com.huang.rpc.server.handler.Invoker;
import com.huang.rpc.server.handler.RpcInvocation;

/**
 * 对放到指定目录下的service服务
 * @author Administrator
 *
 */
public class ServiceRegistry implements Registry {
    
    // service全限定名称缓存
    public static final List<String> serviceCache = new ArrayList<>();
    
    // 接口名和服务名的映射关系缓存
    public static final Map<String, String> serviceNameCache = new ConcurrentHashMap<>();
    
    // 服务名称和实例映射关系缓存
    private static final Map<String, Object> serviceMapper = new HashMap<>();
    
    @Override
    public void publish(String basePackage) {
        // 获取服务列表
        findServices(basePackage);
        // 服务注册
        // register();
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
    public void registr(URL url) {
        // TODO Auto-generated method stub
    }
    
    /**
     * 通过参数获取调用器
     * @param body 目前为止是具体类型，后面需要改为执行类型
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SecurityException
     * @throws NoSuchMethodException
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
        if (serviceMapper.containsKey(className)) {
            clazz = serviceMapper.get(className);
        } else {
            // 如果全限定名服务不存在，那么直接返回空
            String serviceUniqueName = getServiceName(className);
            if (null == serviceUniqueName) {
                return null;
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
     * @param className
     * @return
     */
    private static String getServiceName(final String className) {
        if (serviceCache.isEmpty()) {
            return null;
        }
        if (serviceNameCache.containsKey(className)) {
            return serviceNameCache.get(className);
        }
        for (String serviceName : serviceCache) {
            try {
                Class<?> clazz = Class.forName(serviceName);
                Class<?>[] interfaces = clazz.getInterfaces();
                for (Class<?> inter : interfaces) {
                    // 找到了实现了接口的服务
                    // TODO:还需要做同名服务的重复判断
                    if (Objects.equals(className, inter.getName())) {
                        serviceNameCache.put(className, serviceName);
                        return serviceName;
                    }
                }
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
        return null;
    }
    
}
