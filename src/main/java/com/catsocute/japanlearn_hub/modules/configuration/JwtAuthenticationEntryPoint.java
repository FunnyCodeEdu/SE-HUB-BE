package com.catsocute.japanlearn_hub.modules.configuration;

import com.catsocute.japanlearn_hub.common.constant.ApiConstant;
import com.catsocute.japanlearn_hub.common.dto.MessageDTO;
import com.catsocute.japanlearn_hub.common.dto.response.GenericResponse;
import com.catsocute.japanlearn_hub.common.enums.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException exception) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.AUTHZ_UNAUTHORIZED;
        GenericResponse<?> genericResponse = GenericResponse.builder()
                .isSuccess(ApiConstant.FAILURE)
                .message(MessageDTO.builder()
                        .messageCode(errorCode.getCode())
                        .messageDetail(errorCode.getMessage())
                        .build())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(errorCode.getHttpStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(genericResponse));
        response.flushBuffer();
    }
}
