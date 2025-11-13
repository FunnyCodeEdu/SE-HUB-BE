package com.se.hub.common.controller;

import com.se.hub.common.constant.ApiConstant;
import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.dto.MessageDTO;
import com.se.hub.common.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    /**
     * Return success response with data and custom message
     *
     * @param data    response data
     * @param message message DTO
     * @param <T>     response type
     * @return ResponseEntity with GenericResponse
     */
    protected <T> ResponseEntity<GenericResponse<T>> success(T data, MessageDTO message) {
        GenericResponse<T> response = GenericResponse.<T>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(message)
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Return success response with data and default message
     *
     * @param data response data
     * @param <T>  response type
     * @return ResponseEntity with GenericResponse
     */
    protected <T> ResponseEntity<GenericResponse<T>> success(T data) {
        MessageDTO defaultMessage = MessageDTO.builder()
                .messageCode(MessageCodeConstant.M001_SUCCESS)
                .messageDetail(MessageConstant.SUCCESS)
                .build();
        return success(data, defaultMessage);
    }

    /**
     * Return success response with data, message code and message detail
     *
     * @param data         response data
     * @param messageCode  message code
     * @param messageDetail message detail
     * @param <T>          response type
     * @return ResponseEntity with GenericResponse
     */
    protected <T> ResponseEntity<GenericResponse<T>> success(T data, String messageCode, String messageDetail) {
        MessageDTO message = MessageDTO.builder()
                .messageCode(messageCode)
                .messageDetail(messageDetail)
                .build();
        return success(data, message);
    }
}

