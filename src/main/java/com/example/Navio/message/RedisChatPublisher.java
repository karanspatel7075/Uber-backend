package com.example.Navio.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisChatPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public void publishChat(String sender, String recipient, String message, Long rideId) {

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "CHAT");
            payload.put("sender", sender);
            payload.put("recipient", recipient);
            payload.put("message", message);
            payload.put("rideId", rideId);

            String json = mapper.writeValueAsString(payload);

            log.info("📩 Publishing CHAT message to Redis for recipient {}", recipient);

            redisTemplate.convertAndSend("chat", json);

        } catch (Exception e) {
            log.error("❌ Error publishing chat message: {}", e.getMessage());
        }
    }
}