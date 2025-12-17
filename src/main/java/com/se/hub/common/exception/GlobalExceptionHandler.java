package com.se.hub.common.exception;

import com.se.hub.common.constant.ApiConstant;
import com.se.hub.common.dto.MessageDTO;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.common.enums.ErrorCode;
import com.se.hub.modules.document.exception.DocumentErrorCode;
import com.se.hub.modules.document.exception.DocumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException{
    
    // Handle DocumentException first (more specific)
    @ExceptionHandler(value = DocumentException.class)
    public ResponseEntity<GenericResponse<Object>> handlingDocumentException(DocumentException exception) {
        DocumentErrorCode documentErrorCode = exception.getDocumentErrorCode();
        String formattedMessage = exception.getFormattedMessage();
        
        GenericResponse<Object> genericResponse = GenericResponse.builder()
                .isSuccess(ApiConstant.FAILURE)
                .message(MessageDTO.builder()
                        .messageCode(documentErrorCode.getCode())
                        .messageDetail(formattedMessage)
                        .build())
                .build();
        return ResponseEntity.status(documentErrorCode.getHttpStatus()).body(genericResponse);
    }
    
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<GenericResponse<Object>> handlingAppException(AppException exception){
        ErrorCode errorCode = exception.getErrorCode();
        GenericResponse<Object> genericResponse = GenericResponse.builder()
                .isSuccess(ApiConstant.FAILURE)
                .message(MessageDTO.builder()
                        .messageCode(errorCode.getCode())
                        .messageDetail(errorCode.getMessage())
                        .build())
                .build();
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(genericResponse);
    }
    
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<GenericResponse<Object>> handlingRuntimeException(RuntimeException exception){
        // Skip if it's an AppException (already handled)
        if (exception instanceof AppException) {
            return handlingAppException((AppException) exception);
        }
        
        log.error(exception.getMessage(), exception);
        GenericResponse<Object> genericResponse = GenericResponse.builder()
                .isSuccess(ApiConstant.FAILURE)
                .message(MessageDTO.builder()
                        .messageCode(ErrorCode.SERVER_UNCATEGORIZED_EXCEPTION.getCode())
                        .messageDetail(ErrorCode.SERVER_UNCATEGORIZED_EXCEPTION.getMessage())
                        .build())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
    }

    //handling File Upload Size Exceeded
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ResponseEntity<GenericResponse<Object>> handlingMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
        GenericResponse<Object> genericResponse = GenericResponse.builder()
                .isSuccess(ApiConstant.FAILURE)
                .message(MessageDTO.builder()
                        .messageCode(ErrorCode.FILE_UPLOAD_SIZE_EXCEEDED.getCode())
                        .messageDetail(ErrorCode.FILE_UPLOAD_SIZE_EXCEEDED.getMessage())
                        .build())
                .build();
        return ResponseEntity.status(ErrorCode.FILE_UPLOAD_SIZE_EXCEEDED.getHttpStatusCode()).body(genericResponse);
    }

    //handling Denied Access
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<GenericResponse<Object>> handlingAccessDeniedException(AccessDeniedException exception) {
        GenericResponse<Object> genericResponse = GenericResponse.builder()
                .isSuccess(ApiConstant.FAILURE)
                .message(MessageDTO.builder()
                        .messageCode(ErrorCode.AUTHZ_UNAUTHORIZED.getCode())
                        .messageDetail(ErrorCode.AUTHZ_UNAUTHORIZED.getMessage())
                        .build())
                .build();
        return ResponseEntity.status(ErrorCode.AUTHZ_UNAUTHORIZED.getHttpStatusCode()).body(genericResponse);
    }

    //handling MethodArgumentNotValidException
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<GenericResponse<Object>> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        ErrorCode errorCode = ErrorCode.valueOf(enumKey);
        GenericResponse<Object> genericResponse = GenericResponse.builder()
                .isSuccess(ApiConstant.FAILURE)
                .message(MessageDTO.builder()
                        .messageCode(errorCode.getCode())
                        .messageDetail(errorCode.getMessage())
                        .build())
                .build();

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(genericResponse);
    }
}
