package com.example.Navio.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatService chatService, StringRedisTemplate redisTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.redisTemplate = redisTemplate;
        this.objectMapper.registerModule(new JavaTimeModule()); // 👈 ADD THIS
    }

    // @MessageMapping handles messages sent to /app/chat
    @MessageMapping("/chat")
    public void processMessage(ChatMessage message) throws JsonProcessingException {
        ChatMessage saved = chatService.saveMessage(message);

        // Publish message JSON to Redis (so all instances forward it)
        redisTemplate.convertAndSend("chat", objectMapper.writeValueAsString(saved));

        // send to recipient's private queue
        // Optionally send locally too (fast-path)
//        messagingTemplate.convertAndSendToUser(
//                message.getRecipient(),
//                "/queue/messages",
//                saved
//        );
    }



    // REST endpoint for chat history (optional)
    @GetMapping("/{rideId}")
    public List<ChatMessage> getMessages(@PathVariable Long rideId) {
        return chatService.getChatHistory(rideId);
    }
}
