package com.se.hub.modules.interaction.dto.response;

import com.se.hub.modules.interaction.enums.ReactionType;
import com.se.hub.modules.interaction.enums.TargetType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReactionDetailResponse {
    String id;
    String userId;
    String userName;
    String userAvatar;
    TargetType targetType;
    String targetId;
    ReactionType reactionType;
    Instant createDate;
    Instant updatedDate;
    String createdBy;
    String updateBy;
}

