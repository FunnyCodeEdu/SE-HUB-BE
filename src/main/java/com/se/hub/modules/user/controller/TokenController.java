package com.se.hub.modules.user.controller;

import com.se.hub.common.constant.ApiConstant;
import com.se.hub.common.constant.MessageCodeConstant;
import com.se.hub.common.dto.MessageDTO;
import com.se.hub.common.dto.response.GenericResponse;
import com.se.hub.common.exception.AppException;
import com.se.hub.modules.user.dto.response.UserInfoResponse;
import com.se.hub.modules.user.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {
    
    private final TokenService tokenService;
    
    @GetMapping("/my-info")
    // Permission checks are disabled globally - only requires authentication
    public ResponseEntity<GenericResponse<UserInfoResponse>> getMyInfo() {
        try {
            UserInfoResponse response = tokenService.getCurrentUserInfo();
            GenericResponse<UserInfoResponse> genericResponse = GenericResponse.<UserInfoResponse>builder()
                    .message(MessageDTO.builder()
                            .messageCode(MessageCodeConstant.M005_RETRIEVED)
                            .messageDetail("User info retrieved successfully")
                            .build())
                    .isSuccess(ApiConstant.SUCCESS)
                    .data(response)
                    .build();
            return ResponseEntity.ok(genericResponse);
        } catch (AppException ex) {
            GenericResponse<UserInfoResponse> genericResponse = GenericResponse.<UserInfoResponse>builder()
                    .message(MessageDTO.builder()
                            .messageCode(ex.getErrorCode().getCode())
                            .messageDetail(ex.getErrorCode().getMessage())
                            .build())
                    .isSuccess(ApiConstant.FAILURE)
                    .build();
            return ResponseEntity.badRequest().body(genericResponse);
        } catch (Exception ex) {
            GenericResponse<UserInfoResponse> genericResponse = GenericResponse.<UserInfoResponse>builder()
                    .message(MessageDTO.builder()
                            .messageCode(MessageCodeConstant.E005_INTERNAL_ERROR)
                            .messageDetail("Internal server error")
                            .build())
                    .isSuccess(ApiConstant.FAILURE)
                    .build();
            return ResponseEntity.status(500).body(genericResponse);
        }
    }
}

