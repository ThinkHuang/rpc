package com.huang.rpc.server.test;

import com.huang.rpc.server.registry.DefaultServiceRegistry;

public class ServiceRegistryTest {
    public static void main(String[] args) {
        DefaultServiceRegistry registry = new DefaultServiceRegistry();
        registry.publish("com.huang.rpc.server.service");
        System.out.println(DefaultServiceRegistry.getServicecache());
    }
}
