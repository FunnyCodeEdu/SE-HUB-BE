package com.se.hub.modules.chat.config;

import com.se.hub.modules.chat.constant.SocketEvent;
import com.se.hub.modules.chat.repository.ConversationRepository;
import com.se.hub.modules.chat.service.api.SessionService;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Socket Handler
 * Handles Socket.IO connection events
 * Virtual Thread Best Practice:
 * - This handler uses synchronous blocking I/O operations
 * - Virtual threads automatically handle blocking operations efficiently
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SocketHandler {
    
    SessionService sessionService;
    JwtDecoder jwtDecoder;
    ConversationRepository conversationRepository;
    
    private static final String USER_ID_KEY = "userId";
    private static final String TOKEN_PARAM = "token";
    
    /**
     * Handle client connection
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database operations, enabling high concurrency.
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        try {
            String userId = extractUserIdFromToken(client);
            if (userId != null) {
                // Set userId to client
                client.set(USER_ID_KEY, userId);
                
                // Save session
                sessionService.saveSession(userId, client.getSessionId().toString());
                
                // Join all user's conversations
                joinUserConversations(client, userId);
                
                log.info("SocketHandler_onConnect_Client connected: {} for user: {}", client.getSessionId(), userId);
            } else {
                log.warn("SocketHandler_onConnect_Authentication failed for client: {}", client.getSessionId());
                client.disconnect();
            }
        } catch (Exception e) {
            log.error("SocketHandler_onConnect_Error handling connect", e);
            client.disconnect();
        }
    }
    
    /**
     * Handle client disconnection
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during Redis operations, enabling high concurrency.
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        try {
            Object userIdObj = client.get(USER_ID_KEY);
            if (userIdObj != null) {
                String userId = userIdObj.toString();
                sessionService.removeSession(userId, client.getSessionId().toString());
                log.info("SocketHandler_onDisconnect_Client disconnected: {} for user: {}", client.getSessionId(), userId);
            }
        } catch (Exception e) {
            log.error("SocketHandler_onDisconnect_Error handling disconnect", e);
        }
    }
    
    /**
     * Handle heartbeat event
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during Redis operations, enabling high concurrency.
     */
    @OnEvent(SocketEvent.HEARTBEAT)
    public void onHeartbeat(SocketIOClient client) {
        Object userIdObj = client.get(USER_ID_KEY);
        if (userIdObj != null) {
            String userId = userIdObj.toString();
            sessionService.updateHeartbeat(userId, client.getSessionId().toString());
        }
    }
    
    /**
     * Handle join room event
     */
    @OnEvent(SocketEvent.JOIN_ROOM)
    public void onJoinRoom(SocketIOClient client, String conversationId) {
        Object userIdObj = client.get(USER_ID_KEY);
        if (userIdObj != null) {
            String userId = userIdObj.toString();
            String roomName = "conversation:" + conversationId;
            client.joinRoom(roomName);
            log.debug("SocketHandler_onJoinRoom_User {} joined room: {}", userId, roomName);
        }
    }
    
    /**
     * Handle leave room event
     */
    @OnEvent(SocketEvent.LEAVE_ROOM)
    public void onLeaveRoom(SocketIOClient client, String conversationId) {
        String roomName = "conversation:" + conversationId;
        client.leaveRoom(roomName);
        log.debug("SocketHandler_onLeaveRoom_User left room: {}", roomName);
    }
    
    /**
     * Extract user ID from JWT token in handshake
     * Uses SE-HUB-BE JWT decoder
     */
    private String extractUserIdFromToken(SocketIOClient client) {
        String token = client.getHandshakeData().getSingleUrlParam(TOKEN_PARAM);
        
        if (token != null && !token.isBlank()) {
            try {
                // Decode JWT using SE-HUB-BE JWT decoder
                Jwt jwt = jwtDecoder.decode(token);
                
                // Get userId from JWT claims (subject or userId claim)
                String userId = jwt.getSubject();
                if (userId == null) {
                    Object userIdClaim = jwt.getClaims().get("userId");
                    if (userIdClaim != null) {
                        userId = userIdClaim.toString();
                    }
                }
                
                return userId;
            } catch (JwtException e) {
                log.warn("SocketHandler_extractUserIdFromToken_Invalid JWT token: {}", e.getMessage());
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * Join all user's conversations to rooms
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     * Virtual threads yield during database queries, enabling high concurrency.
     */
    private void joinUserConversations(SocketIOClient client, String userId) {
        try {
            // Get all conversations for user (first page only for initial join)
            List<com.se.hub.modules.chat.entity.Conversation> conversations = 
                conversationRepository.findByParticipantIdsContains(userId, 
                    org.springframework.data.domain.PageRequest.of(0, 100))
                    .getContent();
            
            // Join each conversation room
            for (com.se.hub.modules.chat.entity.Conversation conversation : conversations) {
                String roomName = "conversation:" + conversation.getConversationId();
                client.joinRoom(roomName);
            }
            
            log.debug("SocketHandler_joinUserConversations_User {} joined {} conversation rooms", userId, conversations.size());
        } catch (Exception e) {
            log.error("SocketHandler_joinUserConversations_Error joining user conversations", e);
            // Don't disconnect - allow connection but user can manually join rooms
        }
    }
}

