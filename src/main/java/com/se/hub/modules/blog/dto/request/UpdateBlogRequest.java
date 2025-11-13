package com.se.hub.modules.blog.dto.request;

import com.se.hub.modules.blog.constant.BlogConstants;
import com.se.hub.modules.blog.constant.BlogErrorCodeConstants;
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
public class UpdateBlogRequest {
    /**
     * Content of the blog. Optional for partial update.
     * If provided, must not be blank and must not exceed max length.
     */
    @Size(max = BlogConstants.CONTENT_MAX_LENGTH,
            message = BlogErrorCodeConstants.BLOG_CONTENT_INVALID)
    String content;

    /**
     * Cover image URL. Optional for partial update.
     */
    String coverImageUrl;

    /**
     * Allow comments flag. Optional for partial update.
     */
    Boolean allowComments;
}
