package com.se.hub.modules.document.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.course.entity.Course;
import com.se.hub.modules.document.constant.DocumentConstants;
import com.se.hub.modules.document.constant.DocumentErrorCodeConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = DocumentConstants.TABLE_DOCUMENT)
@Entity
public class Document extends BaseEntity {
    @NotNull(message = DocumentErrorCodeConstants.DOCUMENT_COURSE_INVALID)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = DocumentConstants.COL_COURSE_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Course course;

    @NotBlank(message = DocumentErrorCodeConstants.DOCUMENT_NAME_INVALID)
    @Size(max = DocumentConstants.DOCUMENT_NAME_MAX_LENGTH,
            message = DocumentErrorCodeConstants.DOCUMENT_NAME_INVALID)
    @Column(name = DocumentConstants.COL_DOCUMENT_NAME,
            nullable = false,
            columnDefinition = DocumentConstants.DOCUMENT_NAME_DEFINITION)
    String documentName;

    @Size(max = DocumentConstants.DESCRIPT_MAX_LENGTH,
            message = DocumentErrorCodeConstants.DOCUMENT_DESCRIPT_INVALID)
    @Column(name = DocumentConstants.COL_DESCRIPT,
            columnDefinition = DocumentConstants.DESCRIPT_DEFINITION)
    String descript;

    @Size(max = DocumentConstants.SEMESTER_MAX_LENGTH,
            message = DocumentErrorCodeConstants.DOCUMENT_SEMESTER_INVALID)
    @Column(name = DocumentConstants.COL_SEMESTER,
            columnDefinition = DocumentConstants.SEMESTER_DEFINITION)
    String semester;

    @Size(max = DocumentConstants.MAJOR_MAX_LENGTH,
            message = DocumentErrorCodeConstants.DOCUMENT_MAJOR_INVALID)
    @Column(name = DocumentConstants.COL_MAJOR,
            columnDefinition = DocumentConstants.MAJOR_DEFINITION)
    String major;

    @NotBlank(message = DocumentErrorCodeConstants.DOCUMENT_NAME_INVALID)
    @Column(name = DocumentConstants.COL_UPLOADED_BY,
            nullable = false)
    String uploadedBy;

    @Size(max = DocumentConstants.FILE_PATH_MAX_LENGTH)
    @Column(name = DocumentConstants.COL_FILE_PATH,
            columnDefinition = DocumentConstants.FILE_PATH_DEFINITION)
    String filePath;

    @Size(max = DocumentConstants.FILE_TYPE_MAX_LENGTH)
    @Column(name = DocumentConstants.COL_FILE_TYPE,
            columnDefinition = DocumentConstants.FILE_TYPE_DEFINITION)
    String fileType;

    @Column(name = DocumentConstants.COL_FILE_SIZE)
    Long fileSize;

    @Builder.Default
    @Column(name = DocumentConstants.COL_IS_APPROVED,
            nullable = false)
    Boolean isApproved = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return getId() != null && getId().equals(document.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}

