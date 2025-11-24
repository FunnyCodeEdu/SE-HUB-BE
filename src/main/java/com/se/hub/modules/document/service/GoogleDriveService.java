package com.se.hub.modules.document.service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.se.hub.modules.document.exception.DocumentErrorCode;
import com.se.hub.modules.document.exception.DocumentException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Google Drive Service
 * Handles file upload, download, and permission management for Google Drive
 */
@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoogleDriveService {
    
    @Value("${google.drive.folder.id:}")
    String folderId;
    
    @Value("${google.drive.callback.url:http://localhost:8080/api/drive/callback}")
    String callbackUrl;
    
    @Value("${google.drive.client_id:}")
    String clientId;
    
    @Value("${google.drive.client_secret:}")
    String clientSecret;
    
    @Value("${google.drive.project_id:}")
    String projectId;
    
    @Value("${google.drive.tokens.dir:tokens}")
    String tokensDir;
    
    @Value("${google.drive.application.name:SE-HUB}")
    String applicationName;
    
    /**
     * Get Drive instance, create new instance each time (no caching)
     * This allows dynamic token refresh without restarting
     */
    private Drive getDrive() {
        try {
            final com.google.api.client.http.javanet.NetHttpTransport HTTP_TRANSPORT = 
                    com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport();
            
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
            
            com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets clientSecrets = 
                    com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.load(
                    com.google.api.client.json.gson.GsonFactory.getDefaultInstance(), 
                    new java.io.StringReader(credentialsJson));
            
            com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow flow = 
                    new com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, 
                    com.google.api.client.json.gson.GsonFactory.getDefaultInstance(), 
                    clientSecrets, 
                    java.util.List.of(com.google.api.services.drive.DriveScopes.DRIVE))
                    .setDataStoreFactory(new com.google.api.client.util.store.FileDataStoreFactory(new java.io.File(tokensDir)))
                    .setAccessType("offline")
                    .build();
            
            // Try to load existing credential
            com.google.api.client.auth.oauth2.Credential credential = flow.loadCredential("user");
            if (credential == null) {
                // Generate actual Google OAuth URL
                String googleAuthUrl = generateGoogleAuthUrl();
                log.error("GoogleDriveService_getDrive_Credential is null. Authorization URL: {}", googleAuthUrl);
                throw DocumentErrorCode.DOCUMENT_GOOGLE_DRIVE_NOT_CONFIGURED.toException(googleAuthUrl);
            }

            ensureCredentialValidity(credential);
            
            Drive drive = new Drive.Builder(HTTP_TRANSPORT, 
                    com.google.api.client.json.gson.GsonFactory.getDefaultInstance(), 
                    credential)
                    .setApplicationName(applicationName)
                    .build();
            
            log.debug("GoogleDriveService_getDrive_Google Drive client initialized successfully");
            return drive;
            
        } catch (Exception e) {
            if (e instanceof com.se.hub.modules.document.exception.DocumentException) {
                throw (com.se.hub.modules.document.exception.DocumentException) e;
            }
            log.error("GoogleDriveService_getDrive_Failed to initialize Google Drive client: {}", e.getMessage(), e);
            String googleAuthUrl = generateGoogleAuthUrl();
            throw DocumentErrorCode.DOCUMENT_GOOGLE_DRIVE_NOT_CONFIGURED.toException(googleAuthUrl);
        }
    }

    private void ensureCredentialValidity(com.google.api.client.auth.oauth2.Credential credential) {
        try {
            boolean hasRefreshToken = credential.getRefreshToken() != null && !credential.getRefreshToken().isBlank();
            Long expiresInSeconds = credential.getExpiresInSeconds();
            boolean aboutToExpire = expiresInSeconds != null && expiresInSeconds <= 60;
            boolean missingAccessToken = credential.getAccessToken() == null || credential.getAccessToken().isBlank();

            if ((aboutToExpire || missingAccessToken) && hasRefreshToken) {
                log.info("GoogleDriveService_ensureCredentialValidity_Refreshing Google token");
                boolean refreshed = credential.refreshToken();
                if (!refreshed) {
                    log.error("GoogleDriveService_ensureCredentialValidity_Refresh token failed (no exception). Requesting re-auth.");
                    throw DocumentErrorCode.DOCUMENT_GOOGLE_DRIVE_NOT_CONFIGURED.toException(generateGoogleAuthUrl());
                }
                log.info("GoogleDriveService_ensureCredentialValidity_Token refreshed successfully");
            } else if ((aboutToExpire || missingAccessToken) && !hasRefreshToken) {
                log.warn("GoogleDriveService_ensureCredentialValidity_No refresh token available and token is invalid/expired");
                throw DocumentErrorCode.DOCUMENT_GOOGLE_DRIVE_NOT_CONFIGURED.toException(generateGoogleAuthUrl());
            }
        } catch (DocumentException e) {
            throw e;
        } catch (com.google.api.client.auth.oauth2.TokenResponseException e) {
            log.error("GoogleDriveService_ensureCredentialValidity_Token refresh failed: {}", e.getDetails(), e);
            throw DocumentErrorCode.DOCUMENT_GOOGLE_DRIVE_NOT_CONFIGURED.toException(generateGoogleAuthUrl());
        } catch (IOException e) {
            log.error("GoogleDriveService_ensureCredentialValidity_IO error while refreshing token: {}", e.getMessage(), e);
            throw DocumentErrorCode.DOCUMENT_GOOGLE_DRIVE_NOT_CONFIGURED.toException(generateGoogleAuthUrl());
        }
    }
    
    /**
     * Generate Google OAuth authorization URL
     * @return Google OAuth authorization URL
     */
    public String generateGoogleAuthUrl() {
        try {
            final com.google.api.client.http.javanet.NetHttpTransport HTTP_TRANSPORT = 
                    com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport();
            
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
                """, 
                clientId, projectId, clientSecret, callbackUrl);
            
            com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets clientSecrets = 
                    com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.load(
                    com.google.api.client.json.gson.GsonFactory.getDefaultInstance(), 
                    new java.io.StringReader(credentialsJson));
            
            com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow flow = 
                    new com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, 
                    com.google.api.client.json.gson.GsonFactory.getDefaultInstance(), 
                    clientSecrets, 
                    java.util.List.of(com.google.api.services.drive.DriveScopes.DRIVE))
                    .setDataStoreFactory(new com.google.api.client.util.store.FileDataStoreFactory(new java.io.File(tokensDir)))
                    .setAccessType("offline")
                    .build();
            
            com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl()
                    .setRedirectUri(callbackUrl)
                    .set("prompt", "consent"); // Force Google to return refresh token every time
            
            return authorizationUrl.build();
        } catch (Exception e) {
            log.error("GoogleDriveService_generateGoogleAuthUrl_Failed to generate Google OAuth URL: {}", e.getMessage(), e);
            // Fallback to localhost URL if generation fails
            String baseUrl = callbackUrl.replace("/api/drive/callback", "");
            return baseUrl + "/api/drive/auth-url";
        }
    }

    /**
     * Upload file to default Google Drive folder and set public view permission
     * @param file MultipartFile to upload
     * @return Google Drive file ID
     * @throws IOException if upload fails
     */
    public String uploadFile(MultipartFile file) throws IOException {
        return uploadFileToFolder(file, null);
    }

    /**
     * Upload file to a specific Google Drive folder (falls back to default folder if empty)
     * @param file MultipartFile to upload
     * @param targetFolderId target Google Drive folder ID
     * @return Google Drive file ID
     * @throws IOException if upload fails
     */
    public String uploadFileToFolder(MultipartFile file, String targetFolderId) throws IOException {
        String effectiveFolderId = resolveFolderId(targetFolderId);
        Drive drive = getDrive();

        log.debug("GoogleDriveService_uploadFile_Uploading file: {} to folder: {}",
                file.getOriginalFilename(), effectiveFolderId != null ? effectiveFolderId : "root");

        File fileMetadata = new File();
        fileMetadata.setName(file.getOriginalFilename());

        // Set parent folder if configured
        if (effectiveFolderId != null) {
            fileMetadata.setParents(java.util.Collections.singletonList(effectiveFolderId));
            log.debug("GoogleDriveService_uploadFile_Setting parent folder: {}", effectiveFolderId);
        }

        java.io.File convFile = convertToFile(file);
        com.google.api.client.http.FileContent mediaContent =
                new com.google.api.client.http.FileContent(file.getContentType(), convFile);

        File uploadedFile = drive.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        String fileId = uploadedFile.getId();
        log.debug("GoogleDriveService_uploadFile_File uploaded with ID: {}", fileId);

        // Set public view permission for everyone
        setPublicViewPermission(fileId);

        // Clean up temporary file
        convFile.delete();

        log.debug("GoogleDriveService_uploadFile_File uploaded successfully with public view permission");
        return fileId;
    }

    private String resolveFolderId(String customFolderId) {
        if (customFolderId != null && !customFolderId.isBlank()) {
            return customFolderId;
        }
        if (folderId != null && !folderId.isBlank()) {
            return folderId;
        }
        return null;
    }

    /**
     * Set public view permission for a file
     * @param fileId Google Drive file ID
     * @throws IOException if permission setting fails
     */
    private void setPublicViewPermission(String fileId) throws IOException {
        Drive drive = getDrive();
        log.debug("GoogleDriveService_setPublicViewPermission_Setting public view permission for file: {}", fileId);
        
        Permission permission = new Permission();
        permission.setType("anyone");
        permission.setRole("reader");

        drive.permissions().create(fileId, permission)
                .setFields("id")
                .execute();
        
        log.debug("GoogleDriveService_setPublicViewPermission_Public view permission set successfully");
    }

    /**
     * Get file download URL
     * @param fileId Google Drive file ID
     * @return Download URL
     */
    public String getFileUrl(String fileId) {
        return "https://drive.google.com/uc?export=download&id=" + fileId;
    }

    /**
     * Get file view URL
     * @param fileId Google Drive file ID
     * @return View URL
     */
    public String getFileViewUrl(String fileId) {
        return "https://drive.google.com/file/d/" + fileId + "/view";
    }

    /**
     * Download file from Google Drive
     * @param fileId Google Drive file ID
     * @return ByteArrayOutputStream containing file content
     * @throws IOException if download fails
     */
    public ByteArrayOutputStream downloadFile(String fileId) throws IOException {
        Drive drive = getDrive();
        log.debug("GoogleDriveService_downloadFile_Downloading file: {}", fileId);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        drive.files().get(fileId).executeMediaAndDownloadTo(outputStream);
        log.debug("GoogleDriveService_downloadFile_File downloaded successfully");
        return outputStream;
    }

    /**
     * Delete file from Google Drive
     * @param fileId Google Drive file ID
     * @throws IOException if deletion fails
     */
    public void deleteFile(String fileId) throws IOException {
        Drive drive = getDrive();
        log.debug("GoogleDriveService_deleteFile_Deleting file: {}", fileId);
        drive.files().delete(fileId).execute();
        log.debug("GoogleDriveService_deleteFile_File deleted successfully");
    }

    /**
     * Convert MultipartFile to java.io.File
     * @param multipartFile MultipartFile to convert
     * @return java.io.File
     * @throws IOException if conversion fails
     */
    private java.io.File convertToFile(MultipartFile multipartFile) throws IOException {
        java.io.File convFile = java.io.File.createTempFile("upload", multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(multipartFile.getBytes());
        }
        return convFile;
    }
}

