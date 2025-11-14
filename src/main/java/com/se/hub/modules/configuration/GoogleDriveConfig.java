package com.se.hub.modules.configuration;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.StringReader;
import java.util.List;

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

