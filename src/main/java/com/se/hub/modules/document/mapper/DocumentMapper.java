package com.se.hub.modules.document.mapper;

import com.se.hub.modules.course.mapper.CourseMapper;
import com.se.hub.modules.document.dto.request.CreateDocumentRequest;
import com.se.hub.modules.document.dto.request.UpdateDocumentRequest;
import com.se.hub.modules.document.dto.response.DocumentResponse;
import com.se.hub.modules.document.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CourseMapper.class})
public interface DocumentMapper {
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "uploadedBy", ignore = true)
    @Mapping(target = "isApproved", ignore = true)
    Document toDocument(CreateDocumentRequest request);

    @Mapping(target = "fileUrl", expression = "java(buildFileUrl(document.getFilePath()))")
    DocumentResponse toDocumentResponse(Document document);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "uploadedBy", ignore = true)
    @Mapping(target = "isApproved", ignore = true)
    Document updateDocumentFromRequest(@MappingTarget Document document, UpdateDocumentRequest request);

    /**
     * Map list of Document entities to list of DocumentResponse DTOs
     * @param documents list of Document entities
     * @return list of DocumentResponse DTOs
     */
    List<DocumentResponse> toListDocumentResponse(List<Document> documents);

    /**
     * Build file URL from file path
     * @param filePath file path
     * @return file URL
     */
    default String buildFileUrl(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
        return "/api/files/" + filename;
    }
}

