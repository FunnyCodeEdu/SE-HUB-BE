package com.se.hub.modules.exam.dto.response;

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
public class QuestionOptionResponse {
    String id;
    String content;
    int orderIndex;
    Boolean isCorrect;
    Instant createDate;
    Instant updatedDate;
    String createdBy;
    String updateBy;
}