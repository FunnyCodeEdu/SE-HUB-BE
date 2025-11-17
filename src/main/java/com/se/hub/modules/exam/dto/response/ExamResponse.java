package com.se.hub.modules.exam.dto.response;

import com.se.hub.modules.exam.enums.ExamType;
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
public class ExamResponse {
    String id;
    String title;
    String description;
    int durationMinutes;
    ExamType examType;
    String examCode;
    String courseId;

    long questionCount;
    ReactionInfo reactions;

    Instant createDate;
    Instant updatedDate;
    String createdBy;
    String updateBy;
}











