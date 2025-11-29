package com.se.hub.modules.blog.dto.response;

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
public class BlogAuthorResponse {
    String id;          // Profile ID
    String userId;      // User ID (for navigation to profile page)
    String username;
    String fullName;
    String avtUrl;
}

