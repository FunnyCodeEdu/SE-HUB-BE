package com.catsocute.japanlearn_hub.modules.lesson.dto.response;

import com.catsocute.japanlearn_hub.modules.lesson.enums.JLPTLevel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * Grammar response DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GrammarResponse {
    String id;
    String title;
    String structure;
    String explanation;
    JLPTLevel level;
    Instant createDate;
    Instant updatedDate;
    String createdBy;
    String updateBy;
}