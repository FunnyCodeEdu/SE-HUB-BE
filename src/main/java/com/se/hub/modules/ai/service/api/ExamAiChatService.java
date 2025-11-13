package com.se.hub.modules.ai.service.api;

import com.se.hub.modules.ai.dto.request.ExamAiChatRequest;
import com.se.hub.modules.ai.dto.response.AiChatResponse;

public interface ExamAiChatService {
    AiChatResponse chat(ExamAiChatRequest request);
}






