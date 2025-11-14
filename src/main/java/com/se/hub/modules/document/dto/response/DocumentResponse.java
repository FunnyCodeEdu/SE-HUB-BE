package com.se.hub.modules.document.dto.response;

import com.se.hub.modules.course.dto.response.CourseResponse;
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
public class DocumentResponse {
    String id;
    CourseResponse course;
    String documentName;
    String descript;
    String semester;
    String major;
    String uploadedBy;
    String filePath;
    String fileType;
    Long fileSize;
    String fileUrl;
    Boolean isApproved;
    Instant createDate;
    Instant updatedDate;
}

