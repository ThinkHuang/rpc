package com.huang.rpc.server.test;

import com.huang.rpc.server.registry.ConcurrentServiceRegistry;

public class ServiceRegistryTest {
    public static void main(String[] args) {
        ConcurrentServiceRegistry registry = new ConcurrentServiceRegistry();
        registry.publish("com.huang.rpc.server.service");
        System.out.println(ConcurrentServiceRegistry.getServicecache());
    }
}
