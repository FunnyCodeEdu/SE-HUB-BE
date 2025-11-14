package com.se.hub.modules.chat.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Participant Info Entity
 * Embedded document for Conversation participants
 * Only stores userId - Profile info will be fetched when needed
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipantInfo {
    String userId;  // Only store userId, fetch profile when needed
}

