package com.se.hub.modules.course.dto.response;

import com.se.hub.modules.course.enums.Specialization;
import com.se.hub.modules.interaction.dto.response.ReactionInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * Course response DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponse {
    String id;
    String name;
    Specialization specialization;
    int semester;
    Instant createDate;
    Instant updatedDate;
    String createdBy;
    String updateBy;
    String description;
    String shortDescription;
    String imgUrl;
    ReactionInfo reactions;
}
