package com.se.hub.modules.auth.controller;

import com.se.hub.common.constant.ApiConstant;
import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.constant.MessageConstant;
import com.se.hub.common.dto.MessageDTO;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.modules.auth.dto.request.AuthenticationRequest;
import com.se.hub.modules.auth.dto.request.IntrospectRequest;
import com.se.hub.modules.auth.dto.response.AuthenticationResponse;
import com.se.hub.modules.auth.dto.response.IntrospectResponse;
import com.se.hub.modules.auth.service.api.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ResponseEntity<GenericResponse<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest request) {
        GenericResponse<AuthenticationResponse> response = GenericResponse.<AuthenticationResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M001_SUCCESS)
                        .messageDetail(MessageConstant.SUCCESS)
                        .build())
                .data(authenticationService.authenticate(request))
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/introspect")
    ResponseEntity<GenericResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request) throws ParseException {
        GenericResponse<IntrospectResponse> response = GenericResponse.<IntrospectResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M001_SUCCESS)
                        .messageDetail(MessageConstant.SUCCESS)
                        .build())
                .data(authenticationService.introspect(request))
                .build();
        return ResponseEntity.ok(response);
    }
}
