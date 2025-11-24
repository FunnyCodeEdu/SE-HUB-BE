package com.se.hub.modules.document.controller;

import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.constant.ResponseCode;
import com.se.hub.common.controller.BaseController;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.modules.document.constant.DocumentMessageConstants;
import com.se.hub.modules.document.dto.response.ImageUploadResponse;
import com.se.hub.modules.document.service.ImageUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@Validated
@Tag(name = "Image Upload", description = "Upload images to Google Drive")
@RequestMapping("/images")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageUploadController extends BaseController {
    ImageUploadService imageUploadService;

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @Operation(summary = "Upload image", description = "Upload an image file to Google Drive and return accessible URLs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK_200, description = DocumentMessageConstants.API_DOCUMENT_IMAGE_UPLOAD_SUCCESS,
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = DocumentMessageConstants.API_BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = DocumentMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<ImageUploadResponse>> uploadImage(@RequestParam("image") MultipartFile image) {
        log.debug("ImageUploadController_uploadImage_Received image upload request: {}", image != null ? image.getOriginalFilename() : "null");
        ImageUploadResponse response = imageUploadService.uploadImage(image);
        return success(response, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }
}

