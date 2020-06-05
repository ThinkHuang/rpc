package com.huang.rpc.server.registry;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractServiceRegistry implements Registry {

    // service全限定名称缓存
    private final List<String> serviceCache = new CopyOnWriteArrayList<>();
    
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

    public List<String> getServiceCache() {
        return serviceCache;
    }
}
