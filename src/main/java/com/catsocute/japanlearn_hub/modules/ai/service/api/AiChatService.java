package com.catsocute.japanlearn_hub.modules.ai.service.api;

import com.catsocute.japanlearn_hub.modules.ai.dto.request.AiChatRequest;
import com.catsocute.japanlearn_hub.modules.ai.dto.response.AiChatResponse;

public interface AiChatService {
    public AiChatResponse chat(AiChatRequest request);
}
