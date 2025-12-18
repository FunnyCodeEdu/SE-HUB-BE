package com.se.hub.modules.document.service;

import com.se.hub.modules.document.dto.response.ImageUploadResponse;
import com.se.hub.modules.document.exception.DocumentErrorCode;
import com.se.hub.modules.document.exception.DocumentException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.se.hub.modules.document.constant.DocumentConstants.IMAGE_MAX_FILE_SIZE_BYTES;
import static com.se.hub.modules.document.constant.DocumentConstants.IMAGE_MAX_FILE_SIZE_MB;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageUploadService {
    GoogleDriveService googleDriveService;

    static final String IMAGE_CONTENT_TYPE_PREFIX = "image/";

    @NonFinal
    @Value("${google.drive.image.folder.id:}")
    String imageFolderId;

    /**
     * Upload an image to Google Drive (public access) and return accessible URLs
     * @param image multipart image file
     * @return ImageUploadResponse containing Drive ID and URLs
     */
    public ImageUploadResponse uploadImage(MultipartFile image) {
        validateImage(image);

        try {
            String fileId = googleDriveService.uploadFileToFolder(image, imageFolderId);
            return ImageUploadResponse.builder()
                    .fileId(fileId)
                    .viewUrl(googleDriveService.getFileViewUrl(fileId))
                    .downloadUrl(googleDriveService.getFileUrl(fileId))
                    .build();
        } catch (IOException e) {
            log.error("ImageUploadService_uploadImage_Failed to upload image: {}", e.getMessage(), e);
            throw DocumentErrorCode.DOCUMENT_UPLOAD_FAILED.toException();
        }
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            log.error("ImageUploadService_validateImage_Image file is required");
            throw DocumentErrorCode.DOCUMENT_FILE_REQUIRED.toException();
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith(IMAGE_CONTENT_TYPE_PREFIX)) {
            log.error("ImageUploadService_validateImage_Invalid image content type: {}", contentType);
            throw DocumentErrorCode.DOCUMENT_IMAGE_INVALID_FORMAT.toException();
        }

        if (image.getSize() > IMAGE_MAX_FILE_SIZE_BYTES) {
            log.error("ImageUploadService_validateImage_Image size {} exceeds limit {} MB", image.getSize(), IMAGE_MAX_FILE_SIZE_MB);
            throw DocumentErrorCode.DOCUMENT_IMAGE_TOO_LARGE.toException(IMAGE_MAX_FILE_SIZE_MB);
        }
    }
}

