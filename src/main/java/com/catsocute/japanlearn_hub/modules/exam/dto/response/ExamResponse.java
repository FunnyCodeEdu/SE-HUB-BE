package com.catsocute.japanlearn_hub.modules.exam.dto.response;

import com.catsocute.japanlearn_hub.modules.exam.enums.ExamType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Set;

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

    Set<QuestionResponse> questions;

    Instant createDate;
    Instant updatedDate;
    String createdBy;
    String updateBy;
}











