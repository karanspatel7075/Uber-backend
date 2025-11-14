package com.example.Navio.websocket;

//Subscriber: side

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class RedisMessageSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LogManager.getLogger(RedisMessageSubscriber.class);

    public RedisMessageSubscriber(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // This will be called for every message on "ride-requests"
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String payload = new String(message.getBody());
            // payload is JSON (we will publish JSON). Example:
            // {"type":"NEW_RIDE","driverId":123,"rideId":456,"data":{...}}

            Map<String, Object> map = mapper.readValue(payload, Map.class);
            String type = (String) map.get("type");

            if("NEW_RIDE".equals(type)) {
                // either broadcast to topic:

                // ✅ Broadcast to all (for logging/debug)
                messagingTemplate.convertAndSend("/topic/ride-requests", map);

                // or send to specific user(s):
                if(map.containsKey("driverUsername")) {
                    String driverEmail = String.valueOf(map.get("driverUsername"));
                    log.info("🎯 Sending private ride request to driver: {}", driverEmail);


                    // In RedisMessageSubscriber
                    log.info("Sending message to user {}", map.get("driverUsername"));

                    System.out.println("📨 Sending message to user: " + driverEmail);
                    messagingTemplate.convertAndSendToUser(
                            driverEmail,  // must match principal name
                            "/queue/requests",  // user-specific queue
                            map);
                }
            } else if("RIDE_UPDATE".equals(type)) {
                messagingTemplate.convertAndSend("/topic/ride-updates", map);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
