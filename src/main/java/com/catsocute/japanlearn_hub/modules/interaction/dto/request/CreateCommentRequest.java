package com.catsocute.japanlearn_hub.modules.interaction.dto.request;

import com.catsocute.japanlearn_hub.modules.interaction.constant.CommentConstants;
import com.catsocute.japanlearn_hub.modules.interaction.constant.CommentErrorCodeConstants;
import com.catsocute.japanlearn_hub.modules.interaction.enums.TargetType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCommentRequest {

    @NotNull(message = CommentErrorCodeConstants.COMMENT_TARGET_TYPE_INVALID)
    @Enumerated(EnumType.STRING)
    TargetType targetType;

    @NotBlank(message = CommentErrorCodeConstants.COMMENT_TARGET_ID_INVALID)
    @NotNull(message = CommentErrorCodeConstants.COMMENT_TARGET_ID_INVALID)
    @Size(max = CommentConstants.TARGET_ID_MAX_LENGTH,
          message = CommentErrorCodeConstants.COMMENT_TARGET_ID_INVALID)
    String targetId;

    @NotBlank(message = CommentErrorCodeConstants.COMMENT_CONTENT_INVALID)
    @NotNull(message = CommentErrorCodeConstants.COMMENT_CONTENT_INVALID)
    @Size(min = CommentConstants.CONTENT_MIN_LENGTH,
          max = CommentConstants.CONTENT_MAX_LENGTH,
          message = CommentErrorCodeConstants.COMMENT_CONTENT_INVALID)
    String content;

    String parentCommentId;
}

