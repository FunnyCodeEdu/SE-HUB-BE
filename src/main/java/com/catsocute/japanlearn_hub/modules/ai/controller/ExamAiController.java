package com.catsocute.japanlearn_hub.modules.ai.controller;

import com.catsocute.japanlearn_hub.common.constant.ApiConstant;
import com.catsocute.japanlearn_hub.common.constant.MessageCodeConstant;
import com.catsocute.japanlearn_hub.common.constant.MessageConstant;
import com.catsocute.japanlearn_hub.common.dto.MessageDTO;
import com.catsocute.japanlearn_hub.common.dto.response.GenericResponse;
import com.catsocute.japanlearn_hub.modules.ai.dto.request.ExamAiChatRequest;
import com.catsocute.japanlearn_hub.modules.ai.dto.response.AiChatResponse;
import com.catsocute.japanlearn_hub.modules.ai.service.api.ExamAiChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai/exam")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamAiController {
    ExamAiChatService examAiChatService;

    @PostMapping("/chat")
    ResponseEntity<GenericResponse<AiChatResponse>> chat(@RequestBody ExamAiChatRequest request) {
        GenericResponse<AiChatResponse> response = GenericResponse.<AiChatResponse>builder()
                .isSuccess(ApiConstant.SUCCESS)
                .message(MessageDTO.builder()
                        .messageCode(MessageCodeConstant.M001_SUCCESS)
                        .messageDetail(MessageConstant.SUCCESS)
                        .build())
                .data(examAiChatService.chat(request))
                .build();
        return ResponseEntity.ok(response);
    }
}






