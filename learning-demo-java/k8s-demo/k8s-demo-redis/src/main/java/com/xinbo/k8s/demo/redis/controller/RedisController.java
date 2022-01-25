package com.xinbo.k8s.demo.redis.controller;

import com.alibaba.fastjson.JSON;
import com.sinch.xms.ApiConnection;
import com.sinch.xms.SinchSMSApi;
import com.sinch.xms.api.MtBatchTextSmsCreate;
import com.sinch.xms.api.MtBatchTextSmsResult;
import com.xinbo.k8s.demo.redis.service.Rabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("mq")
    public String mq() {
        rabbitTemplate.convertAndSend(Rabbit.EXCHANGE, Rabbit.ROUTING_KEY, "hello world");
        return "send message success";
    }

    private static final String SERVICE_PLAN_ID = "582e61e50fbe46c29575f9f9b3ec0ed8";
    private static final String TOKEN = "6246caee04bc461a90e1f9d90eae51b3";
    private static ApiConnection conn;

    public String sms() {

        String SENDER = ""; //Your sinch number
        String[] RECIPIENTS = { "" }; //your mobile phone number

        ApiConnection conn = ApiConnection
                .builder()
                .servicePlanId(SERVICE_PLAN_ID)
                .token(TOKEN)
                .start();
        MtBatchTextSmsCreate message = SinchSMSApi
                .batchTextSms()
                .sender(SENDER)
                .addRecipient(RECIPIENTS)
                .body("Test message from Sinch.")
                .build();
        try {
            // if there is something wrong with the batch
            // it will be exposed in APIError
            MtBatchTextSmsResult batch = conn.createBatch(message);
            System.out.println(batch.id());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("you sent:" + message.body());

        return "OK";
    }


}
