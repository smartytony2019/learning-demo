package com.xinbo.k8s.demo.redis.config;

import com.xinbo.k8s.demo.redis.service.Rabbit;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 熊二
 * @date 2021/2/2 23:01
 * @desc 队列配置
 */
@Configuration
public class RabbitConfig {

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Autowired
    private RabbitAdmin rabbitAdmin;
    @Bean
    public void createExchangeQueue() {
        //#####################   创建交换机  ##########################
        rabbitAdmin.declareExchange(topicExchange());

        //#####################   创建队列  ##########################
        rabbitAdmin.declareQueue(MyQueue());

        //#####################   绑定队列  ##########################
        rabbitAdmin.declareBinding(BindingBuilder.bind(MyQueue()).to(topicExchange()).with(Rabbit.ROUTING_KEY));

    }

    /**
     * 结算队列
     * @return  Queue
     */
    public Queue MyQueue() {
        return QueueBuilder.durable(Rabbit.QUEUE).build();
    }


    /**
     * 主题交换机
     * @return  TopicExchange
     */
    public TopicExchange topicExchange() {
        return new TopicExchange(Rabbit.EXCHANGE);
    }
}
