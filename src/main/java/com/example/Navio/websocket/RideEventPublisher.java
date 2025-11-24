package com.example.Navio.websocket;

import com.example.Navio.model.Ride;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RideEventPublisher {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void publishRideAccepted(Ride ride) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("rideId", ride.getId());
        payload.put("status", "Accepted");

        // Rider topic -> /topic/rider/{riderId}
        messagingTemplate.convertAndSend("/topic/rider/" + ride.getRiderId(), payload);
    }
}
