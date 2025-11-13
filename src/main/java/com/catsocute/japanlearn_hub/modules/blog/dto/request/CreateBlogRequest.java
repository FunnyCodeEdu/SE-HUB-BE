package com.catsocute.japanlearn_hub.modules.blog.dto.request;

import com.catsocute.japanlearn_hub.modules.blog.constant.BlogConstants;
import com.catsocute.japanlearn_hub.modules.blog.constant.BlogErrorCodeConstants;
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
public class CreateBlogRequest {
    @NotBlank(message = BlogErrorCodeConstants.BLOG_CONTENT_INVALID)
    @Size(max = BlogConstants.CONTENT_MAX_LENGTH,
            message = BlogErrorCodeConstants.BLOG_CONTENT_INVALID)
    String content;

    String coverImageUrl;

    @NotNull(message = BlogErrorCodeConstants.BLOG_ALLOW_COMMENTS_INVALID)
    Boolean allowComments;
}
