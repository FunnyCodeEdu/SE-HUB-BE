package com.se.hub.modules.search.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchUserResponse {
    String profileId;
    String userId;
    String fullName;
    String username;
    String avatarUrl;
    String email;
    boolean verified;
    Set<String> roles;
    String status;
}

