package com.example.Navio.websocket;

import com.example.Navio.auth.AuthTokenGen;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

//Phase 1 — WebSocket configuration (STOMP endpoint + handshake interceptor)

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private AuthTokenGen authTokenGen;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new JwtHandshakeInterceptor(authTokenGen))
                .withSockJS()
                .setSuppressCors(true);;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue", "/user");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    // HandshakeInterceptor to attach Principal from JWT
    static class JwtHandshakeInterceptor implements HandshakeInterceptor {
        private final AuthTokenGen authTokenGen;
        JwtHandshakeInterceptor(AuthTokenGen authTokenGen) { this.authTokenGen = authTokenGen; }

        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Map<String, Object> attributes) {
            // Extract session cookie or query param that contains your JWT token
            // Example: token stored in HTTP session under "jwtToken" (like your controllers)
            if (request instanceof org.springframework.http.server.ServletServerHttpRequest servletRequest) {
                HttpSession httpSession = servletRequest.getServletRequest().getSession(false);
                if (httpSession != null) {
                    String token = (String) httpSession.getAttribute("jwtToken");
                    if (token != null) {
                        String username = authTokenGen.getUsernameFromToken(token);
                        // store username as principal-like value
                        attributes.put("username", username);
                    }
                }
            }
            return true;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Exception exception) { }
    }
}
