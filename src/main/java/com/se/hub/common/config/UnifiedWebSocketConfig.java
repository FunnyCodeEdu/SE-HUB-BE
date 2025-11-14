package com.se.hub.common.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Unified WebSocket Gateway Configuration
 * Single WebSocket server for both Chat and Notification using namespaces
 * 
 * Namespaces:
 * - /chat - Chat functionality
 * - /notification - Notification functionality
 * 
 * Best Practice: One WebSocket gateway, multiple namespaces
 */
@Slf4j
@Configuration
public class UnifiedWebSocketConfig {

    @Value("${websocket.port:9092}")
    private int websocketPort;

    @Value("${websocket.host:0.0.0.0}")
    private String websocketHost;

    @Value("${websocket.max-connections:10000}")
    private int maxConnections;

    @Value("${websocket.boss-threads:1}")
    private int bossThreads;

    @Value("${websocket.worker-threads:0}")
    private int workerThreads;

    @Value("${websocket.cors-origins:*}")
    private String corsOrigins;

    @Bean
    public SocketIOServer unifiedSocketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(websocketHost);
        config.setPort(websocketPort);
        config.setAllowCustomRequests(true);
        
        // Connection timeout settings
        config.setUpgradeTimeout(10000);
        config.setPingTimeout(60000);
        config.setPingInterval(25000);
        
        // Max connections limit
        config.setMaxHttpContentLength(1048576); // 1MB
        config.setMaxFramePayloadLength(1048576); // 1MB
        
        // CORS configuration
        config.setOrigin(corsOrigins);
        
        // Configure Netty thread pools for optimal performance
        int actualWorkerThreads = workerThreads > 0 ? workerThreads : Runtime.getRuntime().availableProcessors() * 2;
        
        config.setBossThreads(bossThreads);
        config.setWorkerThreads(actualWorkerThreads);
        
        // Connection management
        config.setFirstDataTimeout(5000);
        config.setRandomSession(true);
        
        SocketIOServer server = new SocketIOServer(config);
        log.info("UnifiedWebSocketConfig_unifiedSocketIOServer_Unified WebSocket Gateway configured on {}:{} with bossThreads={}, workerThreads={}", 
                websocketHost, websocketPort, bossThreads, actualWorkerThreads);
        return server;
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer unifiedSocketIOServer) {
        return new SpringAnnotationScanner(unifiedSocketIOServer);
    }
}

