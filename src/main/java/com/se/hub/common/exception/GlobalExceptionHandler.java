package com.se.hub.common.exception;

import com.se.hub.common.constant.ApiConstant;
import com.se.hub.common.dto.MessageDTO;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.common.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException{
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<GenericResponse<Object>> handlingRuntimeException(RuntimeException exception){
        log.error(exception.getMessage(),exception);
        GenericResponse<Object> genericResponse = GenericResponse.builder()
                .isSuccess(ApiConstant.FAILURE)
                .message(MessageDTO.builder()
                        .messageCode(ErrorCode.SERVER_UNCATEGORIZED_EXCEPTION.getCode())
                        .messageDetail(ErrorCode.SERVER_UNCATEGORIZED_EXCEPTION.getMessage())
                        .build())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
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
    public ResponseEntity<GenericResponse<Object>> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
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
