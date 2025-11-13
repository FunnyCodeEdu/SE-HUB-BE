package com.se.hub.modules.exam.dto.response;

import com.se.hub.modules.exam.enums.QuestionCategory;
import com.se.hub.modules.exam.enums.QuestionDifficulty;
import com.se.hub.modules.exam.enums.QuestionType;
import com.se.hub.modules.lesson.enums.JLPTLevel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionResponse {
    String id;
    String content;
    QuestionType questionType;
    QuestionDifficulty difficulty;
    int score;
    QuestionCategory category;
    JLPTLevel jlptLevel;
    Instant createDate;
    Instant updatedDate;
    String createdBy;
    String updateBy;
    List<QuestionOptionResponse> options;
}