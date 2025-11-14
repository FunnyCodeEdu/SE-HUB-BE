package com.se.hub.modules.document.controller;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.StringReader;
import java.util.List;

/**
 * Google Drive OAuth Callback Controller
 * Handles OAuth2 callback from Google Drive authorization
 */
@Slf4j
@RestController
@RequestMapping("/drive")
public class GoogleDriveCallbackController {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${google.drive.client_id:}")
    private String clientId;

    @Value("${google.drive.client_secret:}")
    private String clientSecret;

    @Value("${google.drive.project_id:}")
    private String projectId;

    @Value("${google.drive.tokens.dir:tokens}")
    private String tokensDir;

    @Value("${google.drive.callback.url:http://localhost:8080/api/drive/callback}")
    private String callbackUrl;

    /**
     * OAuth callback endpoint
     * This endpoint is called by Google after user authorization
     * 
     * @param code Authorization code from Google (if successful)
     * @param error Error code from Google (if failed)
     * @param errorDescription Error description from Google (if failed)
     * @return Success or error message
     */
    @GetMapping("/callback")
    public ResponseEntity<String> callback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "error_description", required = false) String errorDescription) {
        
        // Check for error first
        if (error != null) {
            log.error("GoogleDriveCallbackController_callback_OAuth error: {} - {}", error, errorDescription);
            
            String errorMessage = errorDescription != null ? errorDescription : "An error occurred during authorization. Error: " + error;
            
            // Provide specific guidance for access_denied error
            if ("access_denied".equals(error)) {
                errorMessage = buildAccessDeniedMessage();
            }
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorHtml("Authorization Failed", errorMessage));
        }
        
        // Check if code is present
        if (code == null || code.isEmpty()) {
            log.error("GoogleDriveCallbackController_callback_No authorization code received");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorHtml("Authorization Failed", "No authorization code received from Google"));
        }
        
        log.info("GoogleDriveCallbackController_callback_Received authorization code. Callback URL: {}", callbackUrl);
        
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            // Build credentials JSON string
            String credentialsJson = String.format("""
                {
                  "web": {
                    "client_id": "%s",
                    "project_id": "%s",
                    "auth_uri": "https://accounts.google.com/o/oauth2/auth",
                    "token_uri": "https://oauth2.googleapis.com/token",
                    "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
                    "client_secret": "%s",
                    "redirect_uris": ["%s"]
                  }
                }
                """, clientId, projectId, clientSecret, callbackUrl);

            log.debug("GoogleDriveCallbackController_callback_Loading client secrets");
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                    JSON_FACTORY, new StringReader(credentialsJson));

            log.debug("GoogleDriveCallbackController_callback_Creating authorization flow. Tokens dir: {}", tokensDir);
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, List.of(DriveScopes.DRIVE))
                    .setDataStoreFactory(new FileDataStoreFactory(new File(tokensDir)))
                    .setAccessType("offline")
                    .build();

            // Exchange authorization code for access token
            log.debug("GoogleDriveCallbackController_callback_Exchanging authorization code for token. Redirect URI: {}", callbackUrl);
            TokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(callbackUrl)
                    .execute();

            log.debug("GoogleDriveCallbackController_callback_Token received. Storing credential...");
            log.debug("GoogleDriveCallbackController_callback_Token has refresh token: {}", tokenResponse.getRefreshToken() != null);
            
            // Store credential
            flow.createAndStoreCredential(tokenResponse, "user");

            log.info("GoogleDriveCallbackController_callback_OAuth authorization successful. Token stored in: {}", tokensDir);
            return ResponseEntity.ok(buildSuccessHtml());

        } catch (Exception e) {
            log.error("GoogleDriveCallbackController_callback_Authorization failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorHtml("Authorization Failed", 
                            "Failed to exchange authorization code for token: " + e.getMessage()));
        }
    }
    
    /**
     * Build success HTML page
     */
    private String buildSuccessHtml() {
        return """
            <html>
            <head>
                <title>Authorization Successful</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        height: 100vh;
                        margin: 0;
                        background-color: #f5f5f5;
                    }
                    .container {
                        text-align: center;
                        background: white;
                        padding: 40px;
                        border-radius: 8px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                        max-width: 500px;
                    }
                    .success {
                        color: #4CAF50;
                        font-size: 24px;
                        margin-bottom: 20px;
                    }
                    .message {
                        color: #333;
                        font-size: 16px;
                        margin-bottom: 10px;
                    }
                    .note {
                        color: #666;
                        font-size: 14px;
                        margin-top: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="success">✓ Authorization Successful!</div>
                    <div class="message">Google Drive access has been authorized.</div>
                    <div class="note">Please restart your application for the changes to take effect.</div>
                    <div class="note">You can close this window now.</div>
                </div>
            </body>
            </html>
            """;
    }
    
    /**
     * Build error HTML page
     */
    private String buildErrorHtml(String title, String message) {
        return String.format("""
            <html>
            <head>
                <title>%s</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        height: 100vh;
                        margin: 0;
                        background-color: #f5f5f5;
                    }
                    .container {
                        text-align: center;
                        background: white;
                        padding: 40px;
                        border-radius: 8px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                        max-width: 500px;
                    }
                    .error {
                        color: #f44336;
                        font-size: 24px;
                        margin-bottom: 20px;
                    }
                    .message {
                        color: #333;
                        font-size: 16px;
                        word-wrap: break-word;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="error">✗ %s</div>
                    <div class="message">%s</div>
                </div>
            </body>
            </html>
            """, title, title, message);
    }
    
    /**
     * Build access denied message with instructions
     */
    private String buildAccessDeniedMessage() {
        return """
            <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                <h2 style="color: #f44336;">Access Denied - Google OAuth Verification Required</h2>
                <p style="font-size: 16px; line-height: 1.6;">
                    The application is currently in <strong>Testing mode</strong> and can only be accessed by developer-approved test users.
                </p>
                <h3 style="color: #333; margin-top: 20px;">To fix this issue:</h3>
                <ol style="line-height: 1.8; padding-left: 20px;">
                    <li>Go to <a href="https://console.cloud.google.com/" target="_blank">Google Cloud Console</a></li>
                    <li>Select your project</li>
                    <li>Navigate to <strong>APIs & Services</strong> → <strong>OAuth consent screen</strong></li>
                    <li>Scroll down to <strong>Test users</strong> section</li>
                    <li>Click <strong>+ ADD USERS</strong></li>
                    <li>Add your email address: <code style="background: #f5f5f5; padding: 2px 6px; border-radius: 3px;">funnycode.softwareengineering@gmail.com</code></li>
                    <li>Click <strong>SAVE</strong></li>
                    <li>Try authorizing again</li>
                </ol>
                <p style="margin-top: 20px; padding: 15px; background: #fff3cd; border-left: 4px solid #ffc107; border-radius: 4px;">
                    <strong>Note:</strong> If you want to make the app available to all users without adding test users, 
                    you need to publish the app (requires Google verification process).
                </p>
                <p style="margin-top: 15px;">
                    <a href="/api/drive/auth-url" style="display: inline-block; padding: 10px 20px; background: #4CAF50; color: white; text-decoration: none; border-radius: 4px;">
                        Try Again
                    </a>
                </p>
            </div>
            """;
    }

    /**
     * Get authorization URL
     * Call this endpoint to get the authorization URL for OAuth
     * 
     * @return Authorization URL
     */
    @GetMapping("/auth-url")
    public ResponseEntity<String> getAuthUrl() {
        log.debug("GoogleDriveCallbackController_getAuthUrl_Generating authorization URL");
        
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            String credentialsJson = String.format("""
                {
                  "web": {
                    "client_id": "%s",
                    "project_id": "%s",
                    "auth_uri": "https://accounts.google.com/o/oauth2/auth",
                    "token_uri": "https://oauth2.googleapis.com/token",
                    "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
                    "client_secret": "%s",
                    "redirect_uris": ["%s"]
                  }
                }
                """, clientId, projectId, clientSecret, callbackUrl);

            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                    JSON_FACTORY, new StringReader(credentialsJson));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, List.of(DriveScopes.DRIVE))
                    .setDataStoreFactory(new FileDataStoreFactory(new File(tokensDir)))
                    .setAccessType("offline")
                    .build();

            AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl()
                    .setRedirectUri(callbackUrl)
                    .set("prompt", "consent"); // Force Google to return refresh token every time

            log.debug("GoogleDriveCallbackController_getAuthUrl_Authorization URL generated: {}", authorizationUrl);
            return ResponseEntity.ok(authorizationUrl.build());

        } catch (Exception e) {
            log.error("GoogleDriveCallbackController_getAuthUrl_Failed to generate authorization URL: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate authorization URL: " + e.getMessage());
        }
    }
}

