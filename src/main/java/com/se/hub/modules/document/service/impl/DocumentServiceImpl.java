package com.se.hub.modules.document.service.impl;

import com.se.hub.common.constant.GlobalVariable;
import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.common.utils.PagingUtil;
import com.se.hub.modules.auth.utils.AuthUtils;
import com.se.hub.modules.course.entity.Course;
import com.se.hub.modules.course.repository.CourseRepository;
import com.se.hub.modules.document.dto.request.CreateDocumentRequest;
import com.se.hub.modules.document.dto.request.UpdateDocumentRequest;
import com.se.hub.modules.document.dto.response.DocumentResponse;
import com.se.hub.modules.document.entity.Document;
import com.se.hub.modules.interaction.dto.response.ReactionInfo;
import com.se.hub.modules.interaction.enums.TargetType;
import com.se.hub.modules.interaction.service.api.ReactionService;
import com.se.hub.modules.document.exception.DocumentErrorCode;
import com.se.hub.modules.document.mapper.DocumentMapper;
import com.se.hub.modules.document.repository.DocumentRepository;
import com.se.hub.modules.document.service.DocumentService;
import com.se.hub.modules.document.service.GoogleDriveService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.se.hub.modules.document.constant.DocumentConstants.DOCUMENT_MAX_FILE_SIZE_BYTES;
import static com.se.hub.modules.document.constant.DocumentConstants.DOCUMENT_MAX_FILE_SIZE_MB;

/**
 * Document Service Implementation
 * 
 * Virtual Thread Best Practice:
 * - This service uses synchronous blocking I/O operations (JPA repository calls)
 * - Virtual threads automatically handle blocking operations efficiently
 * - No need to use CompletableFuture or reactive APIs
 * - Each method call will run on a virtual thread, allowing high concurrency
 * - Database operations are blocking but virtual threads handle them efficiently
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DocumentServiceImpl implements DocumentService {
    DocumentRepository documentRepository;
    CourseRepository courseRepository;
    DocumentMapper documentMapper;
    GoogleDriveService googleDriveService;
    ReactionService reactionService;

    /**
     * Helper method to build PagingResponse from Page<Document>
     */
    private PagingResponse<DocumentResponse> buildPagingResponse(Page<Document> documents) {
        List<Document> documentList = documents.getContent();
        String currentUserId = AuthUtils.getCurrentUserId();
        
        // Batch check reactions for all documents
        List<String> documentIds = documentList.stream().map(Document::getId).toList();
        Map<String, ReactionInfo> reactionsMap = reactionService
                .getReactionsForTargets(TargetType.DOCUMENT, documentIds, currentUserId);
        
        return PagingResponse.<DocumentResponse>builder()
                .currentPage(documents.getNumber())
                .totalPages(documents.getTotalPages())
                .pageSize(documents.getSize())
                .totalElement(documents.getTotalElements())
                .data(documentList.stream()
                        .map(document -> {
                            DocumentResponse response = documentMapper.toDocumentResponse(document);
                            ReactionInfo reactionInfo = reactionsMap.getOrDefault(
                                    document.getId(),
                                    ReactionInfo.builder().userReacted(false).type(null).build()
                            );
                            response.setReactions(reactionInfo);
                            return response;
                        })
                        .toList()
                )
                .build();
    }

    @Override
    @Transactional
    public DocumentResponse createDocument(CreateDocumentRequest request, MultipartFile file) {
        log.debug("DocumentService_createDocument_Creating new document for user: {} with file: {}", 
                AuthUtils.getCurrentUserId(), file.getOriginalFilename());
        String userId = AuthUtils.getCurrentUserId();

        // Validate file
        if (file == null || file.isEmpty()) {
            log.error("DocumentService_createDocument_File is required");
            throw DocumentErrorCode.DOCUMENT_FILE_REQUIRED.toException();
        }
        validateDocumentFile(file);

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> {
                    log.error("DocumentService_createDocument_Course not found with id: {}", request.getCourseId());
                    return DocumentErrorCode.DOCUMENT_COURSE_NOT_FOUND.toException();
                });

        // Upload file to Google Drive
        String fileId;
        try {
            fileId = googleDriveService.uploadFile(file);
            log.debug("DocumentService_createDocument_File uploaded to Google Drive with ID: {}", fileId);
        } catch (com.se.hub.modules.document.exception.DocumentException e) {
            // Re-throw DocumentException to preserve error message with link
            throw e;
        } catch (IOException e) {
            log.error("DocumentService_createDocument_Failed to upload file to Google Drive: {}", e.getMessage(), e);
            throw DocumentErrorCode.DOCUMENT_UPLOAD_FAILED.toException();
        }

        // Build file path (Google Drive view URL)
        String filePath = googleDriveService.getFileViewUrl(fileId);
        String fileType = file.getContentType();
        Long fileSize = file.getSize();

        Document document = documentMapper.toDocument(request);
        document.setCourse(course);
        document.setUploadedBy(userId);
        document.setIsApproved(false); // User creates document - pending approval
        document.setCreatedBy(userId);
        document.setUpdateBy(userId);
        document.setFilePath(filePath);
        document.setFileType(fileType);
        document.setFileSize(fileSize);

        DocumentResponse response = documentMapper.toDocumentResponse(documentRepository.save(document));
        log.debug("DocumentService_createDocument_Document created successfully with id: {}", response.getId());
        return response;
    }

    @Override
    public DocumentResponse getById(String documentId) {
        log.debug("DocumentService_getById_Fetching document with id: {}", documentId);
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> {
                    log.error("DocumentService_getById_Document not found with id: {}", documentId);
                    return DocumentErrorCode.DOCUMENT_NOT_FOUND.toException();
                });

        // Only return approved documents for public access
        if (document.getIsApproved() == null || !document.getIsApproved()) {
            log.warn("DocumentService_getById_Attempted to access unapproved document id: {}", documentId);
            throw DocumentErrorCode.DOCUMENT_UNAPPROVED.toException();
        }

        DocumentResponse response = documentMapper.toDocumentResponse(document);
        String currentUserId = AuthUtils.getCurrentUserId();
        Map<String, ReactionInfo> reactionsMap = reactionService
                .getReactionsForTargets(TargetType.DOCUMENT, List.of(documentId), currentUserId);
        ReactionInfo reactionInfo = reactionsMap.getOrDefault(
                documentId,
                ReactionInfo.builder().userReacted(false).type(null).build()
        );
        response.setReactions(reactionInfo);
        return response;
    }

    @Override
    public PagingResponse<DocumentResponse> getDocumentsByCourseId(String courseId, PagingRequest request) {
        log.debug("DocumentService_getDocumentsByCourseId_Fetching documents for course: {} with page: {}, size: {}",
                courseId, request.getPage(), request.getPageSize());

        // Verify course exists
        if (!courseRepository.existsById(courseId)) {
            log.error("DocumentService_getDocumentsByCourseId_Course not found with id: {}", courseId);
            throw DocumentErrorCode.DOCUMENT_COURSE_NOT_FOUND.toException();
        }

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Document> documents = documentRepository.findAllByCourseIdAndApproved(courseId, pageable);
        log.debug("DocumentService_getDocumentsByCourseId_Found {} documents for course {}", documents.getTotalElements(), courseId);
        return buildPagingResponse(documents);
    }

    @Override
    public PagingResponse<DocumentResponse> getAllDocuments(PagingRequest request) {
        log.debug("DocumentService_getAllDocuments_Fetching documents with page: {}, size: {}",
                request.getPage(), request.getPageSize());

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Document> documents = documentRepository.findAllApproved(pageable);
        log.debug("DocumentService_getAllDocuments_Found {} documents", documents.getTotalElements());
        return buildPagingResponse(documents);
    }

    @Override
    public List<DocumentResponse> getLatestDocuments() {
        log.debug("DocumentService_getLatestDocuments_Fetching latest documents");
        List<Document> documents = documentRepository.findTop4ByIsApprovedTrueOrderByCreateDateDesc();
        String currentUserId = AuthUtils.getCurrentUserId();
        List<String> documentIds = documents.stream().map(Document::getId).toList();
        Map<String, ReactionInfo> reactionsMap = reactionService
                .getReactionsForTargets(TargetType.DOCUMENT, documentIds, currentUserId);
        
        return documents.stream()
                .map(document -> {
                    DocumentResponse response = documentMapper.toDocumentResponse(document);
                    ReactionInfo reactionInfo = reactionsMap.getOrDefault(
                            document.getId(),
                            ReactionInfo.builder().userReacted(false).type(null).build()
                    );
                    response.setReactions(reactionInfo);
                    return response;
                })
                .toList();
    }

    @Override
    public List<DocumentResponse> getSuggestedDocuments(String documentId) {
        log.debug("DocumentService_getSuggestedDocuments_Fetching suggested documents for id: {}", documentId);
        
        // Verify document exists
        if (!documentRepository.existsById(documentId)) {
            log.error("DocumentService_getSuggestedDocuments_Document not found with id: {}", documentId);
            throw DocumentErrorCode.DOCUMENT_NOT_FOUND.toException();
        }

        List<Document> documents = documentRepository.findTop4ByIsApprovedTrueOrderByCreateDateDesc();
        List<Document> filteredDocs = documents.stream()
                .filter(doc -> !doc.getId().equals(documentId))
                .toList();
        
        String currentUserId = AuthUtils.getCurrentUserId();
        List<String> documentIds = filteredDocs.stream().map(Document::getId).toList();
        Map<String, ReactionInfo> reactionsMap = reactionService
                .getReactionsForTargets(TargetType.DOCUMENT, documentIds, currentUserId);
        
        return filteredDocs.stream()
                .map(document -> {
                    DocumentResponse response = documentMapper.toDocumentResponse(document);
                    ReactionInfo reactionInfo = reactionsMap.getOrDefault(
                            document.getId(),
                            ReactionInfo.builder().userReacted(false).type(null).build()
                    );
                    response.setReactions(reactionInfo);
                    return response;
                })
                .toList();
    }

    @Override
    @Transactional
    public DocumentResponse updateDocumentById(String documentId, UpdateDocumentRequest request) {
        log.debug("DocumentService_updateDocumentById_Updating document with id: {}", documentId);
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> {
                    log.error("DocumentService_updateDocumentById_Document not found with id: {}", documentId);
                    return DocumentErrorCode.DOCUMENT_NOT_FOUND.toException();
                });

        document = documentMapper.updateDocumentFromRequest(document, request);
        document.setUpdateBy(AuthUtils.getCurrentUserId());

        DocumentResponse response = documentMapper.toDocumentResponse(documentRepository.save(document));
        log.debug("DocumentService_updateDocumentById_Document updated successfully with id: {}", documentId);
        return response;
    }

    @Override
    @Transactional
    public void deleteDocumentById(String documentId) {
        log.debug("DocumentService_deleteDocumentById_Deleting document with id: {}", documentId);
        if (documentId == null || documentId.isBlank()) {
            log.error("DocumentService_deleteDocumentById_Document ID is required");
            throw DocumentErrorCode.DOCUMENT_ID_REQUIRED.toException();
        }

        if (!documentRepository.existsById(documentId)) {
            log.error("DocumentService_deleteDocumentById_Document not found with id: {}", documentId);
            throw DocumentErrorCode.DOCUMENT_NOT_FOUND.toException();
        }

        documentRepository.deleteById(documentId);
        log.debug("DocumentService_deleteDocumentById_Document deleted successfully with id: {}", documentId);
    }

    @Override
    public PagingResponse<DocumentResponse> getPendingDocuments(PagingRequest request) {
        log.debug("DocumentService_getPendingDocuments_Fetching pending documents with page: {}, size: {}",
                request.getPage(), request.getPageSize());

        // Check admin permission
        checkAdminPermission();

        Pageable pageable = PageRequest.of(
                request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
                request.getPageSize(),
                PagingUtil.createSort(request)
        );

        Page<Document> documents = documentRepository.findAllUnapproved(pageable);
        log.debug("DocumentService_getPendingDocuments_Found {} pending documents", documents.getTotalElements());
        return buildPagingResponse(documents);
    }

    @Override
    @Transactional
    public DocumentResponse approveDocument(String documentId) {
        log.debug("DocumentService_approveDocument_Approving document with id: {}", documentId);

        // Check admin permission
        checkAdminPermission();

        if (documentId == null || documentId.isBlank()) {
            log.error("DocumentService_approveDocument_Document ID is required");
            throw DocumentErrorCode.DOCUMENT_ID_REQUIRED.toException();
        }

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> {
                    log.error("DocumentService_approveDocument_Document not found with id: {}", documentId);
                    return DocumentErrorCode.DOCUMENT_NOT_FOUND.toException();
                });

        // Check if already approved
        if (document.getIsApproved() != null && document.getIsApproved()) {
            log.warn("DocumentService_approveDocument_Document {} is already approved", documentId);
            throw DocumentErrorCode.DOCUMENT_ALREADY_APPROVED.toException();
        }

        // Approve document
        document.setIsApproved(true);
        document.setUpdateBy(AuthUtils.getCurrentUserId());

        DocumentResponse response = documentMapper.toDocumentResponse(documentRepository.save(document));
        log.debug("DocumentService_approveDocument_Document approved successfully with id: {}", documentId);
        return response;
    }

    /**
     * Check if current user is admin
     */
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
    }

    /**
     * Check admin permission and throw exception if not authorized
     */
    private void checkAdminPermission() {
        if (!isAdmin()) {
            log.error("DocumentService_checkAdminPermission_User {} is not admin", AuthUtils.getCurrentUserId());
            throw DocumentErrorCode.DOCUMENT_FORBIDDEN_OPERATION.toException();
        }
    }

    private void validateDocumentFile(MultipartFile file) {
        if (file.getSize() > DOCUMENT_MAX_FILE_SIZE_BYTES) {
            log.error("DocumentService_validateDocumentFile_File size {} exceeds limit {} MB", file.getSize(), DOCUMENT_MAX_FILE_SIZE_MB);
            throw DocumentErrorCode.DOCUMENT_FILE_TOO_LARGE.toException(DOCUMENT_MAX_FILE_SIZE_MB);
        }
    }
}

