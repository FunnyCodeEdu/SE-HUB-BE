package com.se.hub.modules.blog.dto.response;

import com.se.hub.modules.profile.dto.response.ProfileResponse;
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
    ProfileResponse author;
    String content;
    String coverImageUrl;
    int cmtCount;
    int reactionCount;
    Boolean allowComments;
    Instant createDate;
    Instant updatedDate;
}
