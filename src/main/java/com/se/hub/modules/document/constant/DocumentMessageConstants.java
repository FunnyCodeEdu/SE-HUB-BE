package com.se.hub.modules.document.constant;

/**
 * Document Message Constants
 * Contains all message strings for Document module
 */
public class DocumentMessageConstants {
    // Error Messages
    public static final String DOCUMENT_NOT_FOUND_MESSAGE = "Document not found";
    public static final String DOCUMENT_NAME_INVALID_MESSAGE = "Document name is invalid";
    public static final String DOCUMENT_DESCRIPT_INVALID_MESSAGE = "Document description is invalid";
    public static final String DOCUMENT_SEMESTER_INVALID_MESSAGE = "Document semester is invalid";
    public static final String DOCUMENT_MAJOR_INVALID_MESSAGE = "Document major is invalid";
    public static final String DOCUMENT_COURSE_INVALID_MESSAGE = "Document course is invalid";
    public static final String DOCUMENT_ID_REQUIRED_MESSAGE = "Document ID is required";
    public static final String DOCUMENT_COURSE_NOT_FOUND_MESSAGE = "Course not found";
    public static final String DOCUMENT_UNAPPROVED_MESSAGE = "Document is not approved";
    public static final String DOCUMENT_FILE_REQUIRED_MESSAGE = "File is required";
    public static final String DOCUMENT_IMAGE_INVALID_FORMAT_MESSAGE = "Only image files are allowed";
    public static final String DOCUMENT_FILE_TOO_LARGE_MESSAGE = "Document file must not exceed {0} MB";
    public static final String DOCUMENT_IMAGE_TOO_LARGE_MESSAGE = "Image file must not exceed {0} MB";
    public static final String DOCUMENT_UPLOAD_FAILED_MESSAGE = "Failed to upload file to Google Drive";
    public static final String DOCUMENT_GOOGLE_DRIVE_NOT_CONFIGURED_MESSAGE = "Google Drive is not configured. Please authorize first. Authorization URL: {0}";

    // API Response Messages
    public static final String API_DOCUMENT_CREATED_SUCCESS = "Document created successfully";
    public static final String API_DOCUMENT_RETRIEVED_ALL_SUCCESS = "Retrieved all documents successfully";
    public static final String API_DOCUMENT_RETRIEVED_BY_ID_SUCCESS = "Retrieved document by ID successfully";
    public static final String API_DOCUMENT_RETRIEVED_BY_COURSE_SUCCESS = "Retrieved documents by course successfully";
    public static final String API_DOCUMENT_UPDATED_SUCCESS = "Document updated successfully";
    public static final String API_DOCUMENT_DELETED_SUCCESS = "Document deleted successfully";
    public static final String API_DOCUMENT_LATEST_SUCCESS = "Retrieved latest documents successfully";
    public static final String API_DOCUMENT_SUGGESTED_SUCCESS = "Retrieved suggested documents successfully";
    public static final String API_DOCUMENT_PENDING_RETRIEVED_SUCCESS = "Retrieved pending documents successfully";
    public static final String API_DOCUMENT_APPROVED_SUCCESS = "Document approved successfully";
    public static final String API_DOCUMENT_IMAGE_UPLOAD_SUCCESS = "Image uploaded successfully";
    public static final String API_BAD_REQUEST = "Bad request";
    public static final String API_INTERNAL_ERROR = "Internal server error";
    public static final String DOCUMENT_FORBIDDEN_OPERATION_MESSAGE = "You do not have permission to perform this operation";
    public static final String DOCUMENT_ALREADY_APPROVED_MESSAGE = "Document is already approved";

    private DocumentMessageConstants() {
        // Prevent instantiation
    }
}

