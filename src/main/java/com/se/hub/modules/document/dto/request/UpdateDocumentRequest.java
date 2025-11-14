package com.se.hub.modules.document.dto.request;

import com.se.hub.modules.document.constant.DocumentConstants;
import com.se.hub.modules.document.constant.DocumentErrorCodeConstants;
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
public class UpdateDocumentRequest {
    /**
     * Document name. Optional for partial update.
     */
    @Size(max = DocumentConstants.DOCUMENT_NAME_MAX_LENGTH,
            message = DocumentErrorCodeConstants.DOCUMENT_NAME_INVALID)
    String documentName;

    /**
     * Document description. Optional for partial update.
     */
    @Size(max = DocumentConstants.DESCRIPT_MAX_LENGTH,
            message = DocumentErrorCodeConstants.DOCUMENT_DESCRIPT_INVALID)
    String descript;

    /**
     * Semester. Optional for partial update.
     */
    @Size(max = DocumentConstants.SEMESTER_MAX_LENGTH,
            message = DocumentErrorCodeConstants.DOCUMENT_SEMESTER_INVALID)
    String semester;

    /**
     * Major. Optional for partial update.
     */
    @Size(max = DocumentConstants.MAJOR_MAX_LENGTH,
            message = DocumentErrorCodeConstants.DOCUMENT_MAJOR_INVALID)
    String major;

    /**
     * File path. Optional for partial update.
     */
    @Size(max = DocumentConstants.FILE_PATH_MAX_LENGTH)
    String filePath;

    /**
     * File type. Optional for partial update.
     */
    @Size(max = DocumentConstants.FILE_TYPE_MAX_LENGTH)
    String fileType;

    /**
     * File size. Optional for partial update.
     */
    Long fileSize;
}

