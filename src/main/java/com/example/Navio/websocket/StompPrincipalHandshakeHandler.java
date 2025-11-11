package com.example.Navio.websocket;

import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

//Phase 2 — give connections a Principal so you can address users
// ✅ Principal Handler (attach user identity to session)
public class StompPrincipalHandshakeHandler extends DefaultHandshakeHandler {

    protected Principal determineUser(org.springframework.http.server.ServletServerHttpRequest request,
                                      org.springframework.web.socket.WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        String username = (String) attributes.get("username");
        if(username == null) {
            username = "anonymous-" + java.util.UUID.randomUUID();

        }
        String finalUsername = username;
        return () -> finalUsername; // lambda implements Principal.getName()
    }
}
