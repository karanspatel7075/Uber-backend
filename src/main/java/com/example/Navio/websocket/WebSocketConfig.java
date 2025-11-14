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
import java.util.Map;

//Phase 1 — WebSocket configuration (STOMP endpoint + handshake interceptor)

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private AuthTokenGen authTokenGen;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // for messaging also
                .setAllowedOriginPatterns("*") // for messaging also
                .addInterceptors(new JwtHandshakeInterceptor(authTokenGen))
                .setHandshakeHandler(new StompPrincipalHandshakeHandler())   // 👈 ADD THIS LINE
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

            if (request instanceof ServletServerHttpRequest servletRequest) {
                HttpSession httpSession = servletRequest.getServletRequest().getSession(false);
                String username = null;

        /* ------------------------------------------------------------
           1️⃣ Try reading JWT from HttpSession (server-stored token)
           ------------------------------------------------------------ */
                if (httpSession != null) {
                    String token = (String) httpSession.getAttribute("jwtToken");
                    if (token != null) {
                        username = authTokenGen.getUsernameFromToken(token);
                    }
                }

        /* ------------------------------------------------------------
           2️⃣ Try reading token from WebSocket URL:  /ws?token=JWT_HERE
           ------------------------------------------------------------ */
                if (username == null) {
                    String query = servletRequest.getServletRequest().getQueryString();
                    if (query != null && query.contains("token=")) {

                        // Extract token safely
                        String token = query.substring(query.indexOf("token=") + 6);

                        // If there are other params: token=xxx&other=yyy
                        if (token.contains("&")) {
                            token = token.substring(0, token.indexOf("&"));
                        }

                        username = authTokenGen.getUsernameFromToken(token);
                    }
                }

        /* ------------------------------------------------------------
           3️⃣ Store Principal username into WebSocket attributes
           ------------------------------------------------------------ */
                if (username != null) {
                    attributes.put("username", username);
                    System.out.println("🟢 WebSocket Authenticated User = " + username);
                } else {
                    System.out.println("🔴 WebSocket Handshake FAILED → No JWT found!");
                }
            }

            return true;
        }


        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Exception exception) { }
    }
}
