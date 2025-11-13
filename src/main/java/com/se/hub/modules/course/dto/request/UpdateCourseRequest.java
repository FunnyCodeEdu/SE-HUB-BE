package com.se.hub.modules.course.dto.request;

import com.se.hub.modules.course.constant.CourseConstants;
import com.se.hub.modules.course.constant.CourseErrorCodeConstants;
import com.se.hub.modules.course.enums.Specialization;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
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
public class UpdateCourseRequest {
    @NotBlank(message = CourseErrorCodeConstants.COURSE_NAME_INVALID)
    @NotNull(message = CourseErrorCodeConstants.COURSE_NAME_INVALID)
    @Size(min = CourseConstants.NAME_MIN_LENGTH,
            max = CourseConstants.NAME_MAX_LENGTH,
            message = CourseErrorCodeConstants.COURSE_NAME_INVALID)
    String name;

    @NotNull(message = CourseErrorCodeConstants.COURSE_SPECIALIZATION_INVALID)
    @Enumerated(EnumType.STRING)
    Specialization specialization;

    @Min(value = CourseConstants.SEMESTER_MIN,
            message = CourseErrorCodeConstants.COURSE_SEMESTER_INVALID)
    @Max(value = CourseConstants.SEMESTER_MAX,
            message = CourseErrorCodeConstants.COURSE_SEMESTER_INVALID)
    int semester;

    @Size(max = CourseConstants.DESCRIPTION_MAX_LENGTH,
            message = CourseErrorCodeConstants.COURSE_DESCRIPTION_INVALID)
    String description;

    @Size(max = CourseConstants.DESCRIPTION_MAX_LENGTH,
            message = CourseErrorCodeConstants.COURSE_DESCRIPTION_INVALID)
    String shortDescription;

    String imgUrl;
}
