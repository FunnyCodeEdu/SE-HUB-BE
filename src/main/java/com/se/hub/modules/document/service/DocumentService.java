package com.se.hub.modules.document.service;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.document.dto.request.CreateDocumentRequest;
import com.se.hub.modules.document.dto.request.UpdateDocumentRequest;
import com.se.hub.modules.document.dto.response.DocumentResponse;

import java.util.List;

public interface DocumentService {
    /**
     * Create new document with file upload (User endpoint - requires authentication, document will be pending approval)
     * @param request Document creation request
     * @param file File to upload to Google Drive
     * @return DocumentResponse with file URL
     */
    DocumentResponse createDocument(CreateDocumentRequest request, org.springframework.web.multipart.MultipartFile file);

    /**
     * Get document by ID (only approved documents for public access)
     */
    DocumentResponse getById(String documentId);

    /**
     * Get documents by course ID with pagination (only approved documents for public access)
     */
    PagingResponse<DocumentResponse> getDocumentsByCourseId(String courseId, PagingRequest request);

    /**
     * Get all documents with pagination (only approved documents for public access)
     */
    PagingResponse<DocumentResponse> getAllDocuments(PagingRequest request);

    /**
     * Get 4 latest documents by created date (only approved documents)
     */
    List<DocumentResponse> getLatestDocuments();

    /**
     * Get suggested documents (excluding the current document, only approved documents)
     */
    List<DocumentResponse> getSuggestedDocuments(String documentId);

    /**
     * Update document by ID
     */
    DocumentResponse updateDocumentById(String documentId, UpdateDocumentRequest request);

    /**
     * Delete document by ID
     */
    void deleteDocumentById(String documentId);

    /**
     * Get all pending (unapproved) documents with pagination (Admin only)
     */
    PagingResponse<DocumentResponse> getPendingDocuments(PagingRequest request);

    /**
     * Approve document by ID (Admin only)
     */
    DocumentResponse approveDocument(String documentId);
}

