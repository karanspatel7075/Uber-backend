package com.example.Navio.message;

import com.example.Navio.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private ChatMessageRepository messageRepository;

    public ChatService(ChatMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public ChatMessage saveMessage(ChatMessage message) {
        message.setTimestamp(Instant.now());
        return messageRepository.save(message);
    }

    public List<ChatMessage> getChatHistory(Long rideId) {
        return messageRepository.findByRideIdOrderByTimestampAsc(rideId);
    }
}
