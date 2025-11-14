package com.example.Navio.websocket;

import com.example.Navio.message.RedisChatSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//Phase 3 — Redis config + subscriber that forwards to WebSocket

@Configuration
public class RedisWsConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) { // mic
        StringRedisTemplate tpl = new StringRedisTemplate(factory);
        tpl.setKeySerializer(new StringRedisSerializer());
        tpl.setValueSerializer(new StringRedisSerializer());
        return tpl;
    }

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, // receiver
                                            MessageListenerAdapter listenerAdapter,
                                            MessageListenerAdapter chatListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(chatListenerAdapter , new ChannelTopic("chat"));
        container.addMessageListener(listenerAdapter, new PatternTopic("ride-requests"));
        return container;
    }


    @Bean
    public MessageListenerAdapter chatListenerAdapter(RedisChatSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "handleMessage");
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisMessageSubscriber subscriber) {
        return  new MessageListenerAdapter(subscriber, "onMessage");

//        The method "onMessage" is called whenever a message is received.
//                It acts as a “translator” between Redis and your Java class.
    }
}
