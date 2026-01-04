package com.se.hub.modules.course.entity;

import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.course.constant.CourseConstants;
import com.se.hub.modules.course.constant.CourseErrorCodeConstants;
import com.se.hub.modules.course.enums.Specialization;
import com.se.hub.modules.document.entity.Document;
import com.se.hub.modules.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = CourseConstants.TABLE_COURSE)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Course extends BaseEntity {
    @NotBlank(message = CourseErrorCodeConstants.COURSE_NAME_INVALID)
    @Size(min = CourseConstants.NAME_MIN_LENGTH,
          max = CourseConstants.NAME_MAX_LENGTH,
          message = CourseErrorCodeConstants.COURSE_NAME_INVALID)
    @Column(name = CourseConstants.COL_NAME,
            unique = true,
            nullable = false,
            columnDefinition = CourseConstants.NAME_DEFINITION)
    String name;

    @NotNull(message = CourseErrorCodeConstants.COURSE_SPECIALIZATION_INVALID)
    @Enumerated(EnumType.STRING)
    @Column(name = CourseConstants.COL_SPECIALIZATION,
            nullable = false)
    Specialization specialization;

    @NotNull(message = CourseErrorCodeConstants.COURSE_SEMESTER_INVALID)
    @Min(value = CourseConstants.SEMESTER_MIN,
            message = CourseErrorCodeConstants.COURSE_SEMESTER_INVALID)
    @Max(value = CourseConstants.SEMESTER_MAX,
            message = CourseErrorCodeConstants.COURSE_SEMESTER_INVALID)
    @Column(name = CourseConstants.COL_SEMESTER,
            nullable = false)
    int semester;

    @Size(max = CourseConstants.DESCRIPTION_MAX_LENGTH,
          message = CourseErrorCodeConstants.COURSE_DESCRIPTION_INVALID)
    @Column(name = CourseConstants.COL_DESCRIPTION,
            columnDefinition = CourseConstants.DESCRIPTION_DEFINITION)
    String description;

    @Size(max = CourseConstants.DESCRIPTION_MAX_LENGTH,
            message = CourseErrorCodeConstants.COURSE_DESCRIPTION_INVALID)
    @Column(name = CourseConstants.COL_SHORT_DESCRIPTION,
            columnDefinition = CourseConstants.DESCRIPTION_DEFINITION)
    String shortDescription;

    @Column(name = CourseConstants.COL_IMG_URL,
            columnDefinition = CourseConstants.IMG_URL_DEFINITION)
    String imgUrl;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    Set<Document> documents;

    @ManyToMany(fetch = FetchType.LAZY)
    List<User> users;
}
