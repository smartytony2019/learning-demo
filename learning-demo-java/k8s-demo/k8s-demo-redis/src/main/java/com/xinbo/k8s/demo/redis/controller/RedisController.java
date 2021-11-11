package com.xinbo.k8s.demo.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author 熊二
 * @date 2021/11/11 19:29
 * @desc file desc
 */
@RestController
@RequestMapping("redis")
public class RedisController {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String REDIS_KEY = "demo";

    @GetMapping("get")
    public String get() {
        String str = redisTemplate.opsForValue().get(REDIS_KEY);
        return str;
    }

    @GetMapping("set/{value}")
    public String set(@PathVariable String value) {
        redisTemplate.opsForValue().set(REDIS_KEY, value);
        return "success";
    }


}
