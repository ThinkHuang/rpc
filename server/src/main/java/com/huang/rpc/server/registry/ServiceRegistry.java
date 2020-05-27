package com.huang.rpc.server.registry;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.server.handler.Invocation;
import com.huang.rpc.server.handler.Invoker;
import com.huang.rpc.server.handler.RpcInvocation;

/**
 * 对放到指定目录下的service服务
 * @author Administrator
 *
 */
public class ServiceRegistry implements Registry
{
    
    // service全限定名称缓存
    public static final List<String> serviceCache = new ArrayList<>();
    // 服务名称和实例映射关系缓存
    private static final Map<String, Object> serviceMapper = new HashMap<>();
    
    @Override
    public void publish(String basePackage)
    {
        // basePackage = "com.huang.rpc.server.service"
        // 获取服务列表
        findServices(basePackage);
        // 服务注册
        // register();
    }
    
    private void findServices(String basePackage)
    {
        if (null == basePackage || "".equals(basePackage))
        {
            return;
        }
        // 将com.huang.rpc.server.service变为com/huang/rpc/server/service
        String path = basePackage.replaceAll("\\.", "/");
        // 获取资源路径
        URL url = this.getClass().getClassLoader().getResource(path);
        File directory = new File(url.getFile());
        for (File file : directory.listFiles())
        {
            // 判断资源路径是文件还是目录
            if (file.isDirectory())
            {
                findServices(basePackage + "." + file.getName());
            }
            else
            {
                // 如果是文件，那么需要将文件的全限定名称缓存到serviceCache中
                String filename = file.getName();
                if (filename.endsWith(".class"))
                {
                    String fQFileName = basePackage + "." + file.getName();
                    serviceCache.add(fQFileName.replace(".class", ""));
                }
            }
        }
    }
    
    @Override
    public void registr(URL url)
    {
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
            // TODO:这里实现不优雅，需要修改
            clazz = Class.forName(serviceCache.get(0)).newInstance();
            serviceMapper.put(className, clazz);
        }
        String methodName = body.getMethodName();
        Class<?>[] paramTypes = body.getParamTypes();
        Method method = clazz.getClass().getMethod(methodName, paramTypes);
        Object[] paramValues = body.getParamValues();
        return new RpcInvocation(clazz, method, paramValues);
    }
    
}
