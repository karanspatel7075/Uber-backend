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


    // --- FIX APPLIED HERE ---
    @Bean
    public MessageListenerAdapter chatListenerAdapter(RedisChatSubscriber subscriber) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(subscriber, "handleMessage");

        // CRITICAL FIX: Ensure the raw Redis message payload is deserialized as a clean string.
        // This is what allows the handleMessage(String jsonMessage) method to receive clean JSON
        // that the JavaScript client can successfully parse later.
        adapter.setSerializer(new StringRedisSerializer());

        return adapter;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisMessageSubscriber subscriber) {
        // Assuming this subscriber (for 'ride-requests') also expects a String payload:
        MessageListenerAdapter adapter = new MessageListenerAdapter(subscriber, "onMessage");
        adapter.setSerializer(new StringRedisSerializer());
        return adapter;
    }
}
