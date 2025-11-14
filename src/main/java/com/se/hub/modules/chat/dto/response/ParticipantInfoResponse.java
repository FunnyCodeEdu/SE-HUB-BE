package com.se.hub.modules.chat.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Participant Info Response DTO
 * Contains participant information fetched from Profile
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipantInfoResponse {
    String userId;
    String username;      // From Profile
    String fullName;       // From Profile
    String avatarUrl;      // From Profile
}

