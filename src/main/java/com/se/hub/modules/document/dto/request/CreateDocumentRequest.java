package com.se.hub.modules.document.dto.request;

import com.se.hub.modules.document.constant.DocumentConstants;
import com.se.hub.modules.document.constant.DocumentErrorCodeConstants;
import jakarta.validation.constraints.NotBlank;
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
public class CreateDocumentRequest {
    @NotBlank(message = DocumentErrorCodeConstants.DOCUMENT_COURSE_INVALID)
    String courseId;

    @NotBlank(message = DocumentErrorCodeConstants.DOCUMENT_NAME_INVALID)
    @Size(max = DocumentConstants.DOCUMENT_NAME_MAX_LENGTH,
            message = DocumentErrorCodeConstants.DOCUMENT_NAME_INVALID)
    String documentName;

    @Size(max = DocumentConstants.DESCRIPT_MAX_LENGTH,
            message = DocumentErrorCodeConstants.DOCUMENT_DESCRIPT_INVALID)
    String descript;

    @Size(max = DocumentConstants.SEMESTER_MAX_LENGTH,
            message = DocumentErrorCodeConstants.DOCUMENT_SEMESTER_INVALID)
    String semester;

    @Size(max = DocumentConstants.MAJOR_MAX_LENGTH,
            message = DocumentErrorCodeConstants.DOCUMENT_MAJOR_INVALID)
    String major;

    @Size(max = DocumentConstants.FILE_PATH_MAX_LENGTH)
    String filePath;

    @Size(max = DocumentConstants.FILE_TYPE_MAX_LENGTH)
    String fileType;

    Long fileSize;
}

