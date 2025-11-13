package com.se.hub.modules.profile.dto.response;

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
public class UserStatsResponse {
    String id;
    int points;
    int examsDone;
    int cmtCount;
    int documentsUploaded;
    int blogsUploaded;
    int blogsShared;
}
