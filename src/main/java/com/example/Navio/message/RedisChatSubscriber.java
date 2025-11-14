package com.example.Navio.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class RedisChatSubscriber {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisChatSubscriber(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

        public void handleMessage(String message) {
            try {
                Map<String, Object> map = objectMapper.readValue(message, Map.class);

                // Type of event (CHAT)
                String type = (String) map.get("type");

                if ("CHAT".equals(type)) {
                    String recipient = (String) map.get("recipient");

                    log.info("📨 Forwarding chat message to user: {}", recipient);

                    messagingTemplate.convertAndSendToUser(
                            recipient,
                            "/queue/messages",
                            map
                    );
                }

            } catch (Exception e) {
                log.error("❌ Error processing Redis chat message", e);
            }
        }
}
