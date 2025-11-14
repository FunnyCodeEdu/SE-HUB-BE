package com.se.hub.modules.profile.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FtesUserInfoResponse {
    private String id;
    private String username;
    private String email;
    private String status;
    
    @JsonProperty("role")
    private String role; // ADMIN, INSTRUCTOR, USER, etc.
    
    private String created;
    private String createdAt;
    private Boolean referral;
    private String avatar;
    private String fullName;
    private Boolean enable2FA;
}

