package com.xinbo.k8s.demo.redis.handler;

import com.rabbitmq.client.Channel;
import com.xinbo.k8s.demo.redis.service.Rabbit;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TestHandler {

    @RabbitListener(queues = Rabbit.QUEUE)
    public void onMessage(String data, Channel channel, Message message) {
        System.out.println(data);
    }

}
