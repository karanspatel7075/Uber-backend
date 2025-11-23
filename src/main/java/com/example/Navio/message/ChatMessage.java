package com.example.Navio.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Document(collection = "chat_messages")
public class ChatMessage {

    @Id
    private String id;
    private Long rideId;     // associate message with a ride
    private String sender;  // email or username
    private String recipient;
    private String content;
    private Instant timestamp;
    private String type = "CHAT";


}
