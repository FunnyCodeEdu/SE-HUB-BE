package com.se.hub.modules.configuration;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * Google Drive Configuration
 * Configures Google Drive API client with OAuth2 authentication
 */
@Slf4j
@Configuration
public class GoogleDriveConfig {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${google.drive.client_id}")
    private String clientId;

    @Value("${google.drive.client_secret}")
    private String clientSecret;

    @Value("${google.drive.project_id}")
    private String projectId;

    @Value("${google.drive.tokens.dir}")
    private String tokensDir;

    @Value("${google.drive.application.name}")
    private String applicationName;

    @Value("${google.drive.callback.url}")
    private String callbackUrl;

    // Removed @Bean - GoogleDriveService now creates Drive instances dynamically
    // This allows automatic token refresh without restarting the application
}

