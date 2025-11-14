package com.se.hub.modules.chat.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

/**
 * Netty Socket.IO Configuration
 * Configures and manages Socket.IO server lifecycle
 */
@org.springframework.context.annotation.Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NettySocketIOConfig {
    
    SocketHandler socketHandler;
    
    @NonFinal
    @Value("${socketio.port:8099}")
    int socketIOPort;
    
    @NonFinal
    @Value("${socketio.cors.origins:*}")
    String corsOrigins;
    
    @NonFinal
    SocketIOServer socketIOServerInstance;
    
    @Bean
    public SocketIOServer socketIOServer() {
        Configuration configuration = new Configuration();
        configuration.setPort(socketIOPort);
        configuration.setOrigin(corsOrigins);
        
        SocketIOServer server = new SocketIOServer(configuration);
        server.addListeners(socketHandler);
        this.socketIOServerInstance = server;
        
        return server;
    }
    
    /**
     * Start socket server when application is ready
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startSocketServer() {
        if (socketIOServerInstance != null) {
            socketIOServerInstance.start();
            log.info("NettySocketIOConfig_startSocketServer_Socket.IO server started on port: {}", socketIOPort);
        }
    }
    
    /**
     * Stop socket server on application shutdown
     */
    @PreDestroy
    public void stopSocketServer() {
        if (socketIOServerInstance != null) {
            socketIOServerInstance.stop();
            log.info("NettySocketIOConfig_stopSocketServer_Socket.IO server stopped");
        }
    }
}

