package com.catsocute.japanlearn_hub.modules.exam.dto.request;

import com.catsocute.japanlearn_hub.modules.exam.constant.exam.ExamConstants;
import com.catsocute.japanlearn_hub.modules.exam.constant.exam.ExamErrorCodeConstants;
import com.catsocute.japanlearn_hub.modules.exam.enums.ExamType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class UpdateExamRequest {
    @NotBlank(message = ExamErrorCodeConstants.EXAM_TITLE_INVALID)
    @Size(min = ExamConstants.TITLE_MIN_LENGTH,
            max = ExamConstants.TITLE_MAX_LENGTH,
            message = ExamErrorCodeConstants.EXAM_TITLE_INVALID)
    String title;

    @Size(max = ExamConstants.DESCRIPTION_MAX_LENGTH,
            message = ExamErrorCodeConstants.EXAM_DESCRIPTION_INVALID)
    String description;

    @Min(value = ExamConstants.DURATION_MIN,
            message = ExamErrorCodeConstants.EXAM_DURATION_INVALID)
    int durationMinutes;

    @NotNull(message = ExamErrorCodeConstants.EXAM_TYPE_INVALID)
    @Enumerated(EnumType.STRING)
    ExamType examType;

    @NotBlank(message = ExamErrorCodeConstants.EXAM_CODE_INVALID)
    @Size(min = ExamConstants.EXAM_CODE_MIN_LENGTH,
            max = ExamConstants.EXAM_CODE_MAX_LENGTH,
            message = ExamErrorCodeConstants.EXAM_CODE_INVALID)
    String examCode;

    String courseId;
}



