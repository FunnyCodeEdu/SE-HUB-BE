package com.catsocute.japanlearn_hub.modules.ai.service.api;

import com.catsocute.japanlearn_hub.modules.ai.dto.request.ExamAiChatRequest;
import com.catsocute.japanlearn_hub.modules.ai.dto.response.AiChatResponse;

public interface ExamAiChatService {
    AiChatResponse chat(ExamAiChatRequest request);
}






