package com.example.Navio.message;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequestMapping("/api/chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
    }

    // @MessageMapping handles messages sent to /app/chat
    @MessageMapping("/chat")
    public void processMessage(ChatMessage message) {
        ChatMessage saved = chatService.saveMessage(message);

        // send to recipient's private queue
        messagingTemplate.convertAndSendToUser(
                message.getRecipient(),
                "/queue/messages",
                saved
        );
    }

    // REST endpoint for chat history (optional)
    @GetMapping("/{rideId}")
    public List<ChatMessage> getMessages(@PathVariable Long rideId) {
        return chatService.getChatHistory(rideId);
    }
}
