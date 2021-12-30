package com.xinbo.k8s.demo.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author 熊二
 * @date 2021/11/11 19:24
 * @desc file desc
 */
@SpringBootApplication
@EnableDiscoveryClient
public class K8sDemoRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(K8sDemoRedisApplication.class, args);
    }
}
