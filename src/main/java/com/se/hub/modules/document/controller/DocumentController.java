package com.se.hub.modules.document.controller;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.constant.PaginationConstants;
import com.se.hub.common.constant.ResponseCode;
import com.se.hub.common.controller.BaseController;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.request.SortRequest;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.document.constant.DocumentMessageConstants;
import com.se.hub.modules.document.dto.request.CreateDocumentRequest;
import com.se.hub.modules.document.dto.request.UpdateDocumentRequest;
import com.se.hub.modules.document.dto.response.DocumentResponse;
import com.se.hub.modules.document.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "Document Management",
        description = "Document management API")
@RequestMapping("/documents")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class DocumentController extends BaseController {
    DocumentService documentService;

    @PostMapping
    @Operation(summary = "Create new document",
            description = "Create a new document in the system. Document will be pending approval.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = DocumentMessageConstants.API_DOCUMENT_CREATED_SUCCESS,
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = DocumentMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = DocumentMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<DocumentResponse>> createDocument(@Valid @RequestBody CreateDocumentRequest request) {
        DocumentResponse documentResponse = documentService.createDocument(request);
        return success(documentResponse, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all documents",
            description = "Get list of all approved documents with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = DocumentMessageConstants.API_DOCUMENT_RETRIEVED_ALL_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = DocumentMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = DocumentMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<DocumentResponse>>> getAllDocuments(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
    ) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        return success(documentService.getAllDocuments(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/{documentId}")
    @Operation(summary = "Get document by ID",
            description = "Get document information by document ID (only approved documents)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = DocumentMessageConstants.API_DOCUMENT_RETRIEVED_BY_ID_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = DocumentMessageConstants.DOCUMENT_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = DocumentMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<DocumentResponse>> getDocumentById(@PathVariable String documentId) {
        return success(documentService.getById(documentId), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get documents by course ID",
            description = "Get list of approved documents for a specific course with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = DocumentMessageConstants.API_DOCUMENT_RETRIEVED_BY_COURSE_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = DocumentMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = DocumentMessageConstants.DOCUMENT_COURSE_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = DocumentMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<PagingResponse<DocumentResponse>>> getDocumentsByCourseId(
            @PathVariable String courseId,
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction
    ) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(size)
                .sortRequest(new SortRequest(direction, field))
                .build();

        return success(documentService.getDocumentsByCourseId(courseId, request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest documents",
            description = "Get list of 4 most recent approved documents by creation date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = DocumentMessageConstants.API_DOCUMENT_LATEST_SUCCESS,
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = DocumentMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<List<DocumentResponse>>> getLatestDocuments() {
        return success(documentService.getLatestDocuments(), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @GetMapping("/{documentId}/suggestions")
    @Operation(summary = "Get suggested documents",
            description = "Get list of suggested documents related to the specified document (excluding the current document)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = DocumentMessageConstants.API_DOCUMENT_SUGGESTED_SUCCESS,
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = DocumentMessageConstants.DOCUMENT_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = DocumentMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<List<DocumentResponse>>> getSuggestedDocuments(@PathVariable String documentId) {
        return success(documentService.getSuggestedDocuments(documentId), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }

    @PutMapping("/{documentId}")
    @Operation(summary = "Update document",
            description = "Update document information by document ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = DocumentMessageConstants.API_DOCUMENT_UPDATED_SUCCESS),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = DocumentMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = DocumentMessageConstants.DOCUMENT_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = DocumentMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<DocumentResponse>> updateDocument(
            @PathVariable String documentId,
            @Valid @RequestBody UpdateDocumentRequest request) {
        return success(documentService.updateDocumentById(documentId, request), MessageCodeConstant.M003_UPDATED, MessageConstant.UPDATED);
    }

    @DeleteMapping("/{documentId}")
    @Operation(summary = "Delete document",
            description = "Delete a document from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = DocumentMessageConstants.API_DOCUMENT_DELETED_SUCCESS,
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND_404, description = DocumentMessageConstants.DOCUMENT_NOT_FOUND_MESSAGE),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = DocumentMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<Void>> deleteDocument(@PathVariable String documentId) {
        documentService.deleteDocumentById(documentId);
        return success(null, MessageCodeConstant.M004_DELETED, MessageConstant.DELETED);
    }
}

