package com.example.Navio.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class RedisChatSubscriber {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public RedisChatSubscriber(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;

        // 1. Initialize ObjectMapper
        this.objectMapper = new ObjectMapper();
        // 2. Register the JSR310 module to handle Instant
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // Corrected RedisChatSubscriber.java
    public void handleMessage(String jsonMessage) {
        try {
            // 1. Read the JSON back into a ChatMessage object
            ChatMessage message = objectMapper.readValue(jsonMessage, ChatMessage.class);

            String recipient = message.getRecipient();
            log.info("📨 Forwarding chat message to user: {}", recipient);

            // --- FINAL ATTEMPT FIX ---
            // Instead of sending the POJO (message), which Spring will serialize again,
            // serialize it to a clean JSON string BEFORE sending via WebSocket.
            String messageJson = objectMapper.writeValueAsString(message);

            // 2. Forward the complete ChatMessage object as a STRING
            messagingTemplate.convertAndSendToUser(
                    recipient,
                    "/queue/messages",
                    messageJson // Send the clean JSON string
            );

        } catch (Exception e) {
            // ...
        }
    }
}
