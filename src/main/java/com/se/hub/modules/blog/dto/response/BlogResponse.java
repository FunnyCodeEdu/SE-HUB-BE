package com.se.hub.modules.blog.dto.response;

import com.se.hub.modules.interaction.dto.response.ReactionInfo;
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
public class BlogResponse {
    String id;
    BlogAuthorResponse author;
    String content;
    String coverImageUrl;
    int cmtCount;
    int reactionCount;
    int viewCount;
    Boolean allowComments;
    Boolean isApproved;
    ReactionInfo reactions;
    Instant createDate;
    Instant updatedDate;
}
