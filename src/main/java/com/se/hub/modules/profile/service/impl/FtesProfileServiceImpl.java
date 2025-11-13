package com.se.hub.modules.profile.service.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.profile.dto.response.FtesProfileResponse;
import com.se.hub.modules.profile.dto.response.FtesUserInfoResponse;
import com.se.hub.modules.profile.service.FtesProfileService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FtesProfileServiceImpl implements FtesProfileService {

    final WebClient webClient;
    final String ftesApiBaseUrl;

    public FtesProfileServiceImpl(@Value("${ftes.api.base-url:https://api.ftes.vn}") String ftesApiBaseUrl) {
        this.ftesApiBaseUrl = ftesApiBaseUrl;
        this.webClient = WebClient.builder()
                .baseUrl(ftesApiBaseUrl)
                .build();
        log.info("FTES API base URL configured: {}", ftesApiBaseUrl);
    }

    @Override
    public FtesProfileResponse getProfileFromFtes(String userId, String authToken) {
        log.debug("Fetching profile from FTES for userId: {}", userId);
        
        try {
            // FTES API returns ApiResponse<ProfileResponse> wrapper
            ApiResponseWrapper responseWrapper = webClient.get()
                    .uri(ftesApiBaseUrl + "/api/profiles/view/{userId}", userId)
                    .header(HttpHeaders.AUTHORIZATION, authToken)
                    .retrieve()
                    .bodyToMono(ApiResponseWrapper.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            
            if (responseWrapper == null || responseWrapper.getResult() == null) {
                log.warn("FTES API returned null response for userId: {}", userId);
                throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
            }
            
            log.debug("Successfully fetched profile from FTES for userId: {}", userId);
            return responseWrapper.getResult();
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Profile not found in FTES system for userId: {}", userId);
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        } catch (WebClientResponseException e) {
            log.error("Error calling FTES API for userId: {}, status: {}, message: {}", 
                    userId, e.getStatusCode(), e.getMessage());
            throw new AppException(ErrorCode.DATA_INVALID);
        } catch (Exception e) {
            log.error("Unexpected error calling FTES API for userId: {}", userId, e);
            throw new AppException(ErrorCode.DATA_INVALID);
        }
    }
    
    @Override
    public FtesUserInfoResponse getUserInfoFromFtes(String authToken) {
        log.debug("Fetching user info from FTES");
        
        try {
            // FTES API returns ApiResponse<UserInfoResponse> wrapper
            UserInfoApiResponseWrapper responseWrapper = webClient.get()
                    .uri(ftesApiBaseUrl + "/api/users/my-info")
                    .header(HttpHeaders.AUTHORIZATION, authToken)
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .retrieve()
                    .bodyToMono(UserInfoApiResponseWrapper.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            
            if (responseWrapper == null || responseWrapper.getResult() == null) {
                log.warn("FTES API returned null response for user info");
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            
            log.debug("Successfully fetched user info from FTES");
            return responseWrapper.getResult();
        } catch (WebClientResponseException.NotFound e) {
            log.warn("User info not found in FTES system");
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        } catch (WebClientResponseException e) {
            log.error("Error calling FTES API for user info, status: {}, message: {}", 
                    e.getStatusCode(), e.getMessage());
            throw new AppException(ErrorCode.DATA_INVALID);
        } catch (Exception e) {
            log.error("Unexpected error calling FTES API for user info", e);
            throw new AppException(ErrorCode.DATA_INVALID);
        }
    }
    
    /**
     * Wrapper for FTES ApiResponse
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ApiResponseWrapper {
        FtesProfileResponse result;
    }
    
    /**
     * Wrapper for FTES UserInfo ApiResponse
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class UserInfoApiResponseWrapper {
        FtesUserInfoResponse result;
    }
}

