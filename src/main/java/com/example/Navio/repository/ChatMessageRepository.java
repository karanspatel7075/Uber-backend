package com.example.Navio.repository;

import com.example.Navio.message.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByRideIdOrderByTimestampAsc(Long rideId);
}