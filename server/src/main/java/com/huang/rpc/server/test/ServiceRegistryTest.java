package com.huang.rpc.server.test;

import com.huang.rpc.server.registry.ServiceRegistry;

public class ServiceRegistryTest {
    public static void main(String[] args) {
        ServiceRegistry registry = new ServiceRegistry();
        registry.publish("com.huang.rpc.server.service");
        System.out.println(ServiceRegistry.serviceCache);
    }
}
