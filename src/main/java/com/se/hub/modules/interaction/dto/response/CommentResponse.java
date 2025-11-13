package com.se.hub.modules.interaction.dto.response;

import com.se.hub.modules.interaction.enums.TargetType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

/**
 * Comment response DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    String id;
    String authorId;
    String authorName;
    String authorAvatar;
    TargetType targetType;
    String targetId;
    String content;
    String parentCommentId;
    List<CommentResponse> replies;
    Instant createDate;
    Instant updatedDate;
    String createdBy;
    String updateBy;
}

