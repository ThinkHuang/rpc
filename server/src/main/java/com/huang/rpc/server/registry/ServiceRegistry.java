package com.huang.rpc.server.registry;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 对放到指定目录下的service服务
 * @author Administrator
 *
 */
public class ServiceRegistry {
    
    // service全限定名称缓存
    public static final List<String> serviceCache = new ArrayList<>();
    
    public void publish(String basePackage) {
        // basePackage = "com.huang.rpc.server.service"
        // 获取服务列表
        findServices(basePackage);
        // 服务注册
        //doRegister();
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
