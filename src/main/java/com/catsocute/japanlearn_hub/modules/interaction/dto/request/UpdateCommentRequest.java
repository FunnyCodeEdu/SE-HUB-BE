package com.catsocute.japanlearn_hub.modules.interaction.dto.request;

import com.catsocute.japanlearn_hub.modules.interaction.constant.CommentConstants;
import com.catsocute.japanlearn_hub.modules.interaction.constant.CommentErrorCodeConstants;
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
public class UpdateCommentRequest {

    @NotBlank(message = CommentErrorCodeConstants.COMMENT_CONTENT_INVALID)
    @NotNull(message = CommentErrorCodeConstants.COMMENT_CONTENT_INVALID)
    @Size(min = CommentConstants.CONTENT_MIN_LENGTH,
          max = CommentConstants.CONTENT_MAX_LENGTH,
          message = CommentErrorCodeConstants.COMMENT_CONTENT_INVALID)
    String content;
}

