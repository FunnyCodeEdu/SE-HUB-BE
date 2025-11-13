package com.se.hub.modules.ai.service.api;

import com.se.hub.modules.ai.dto.request.AiChatRequest;
import com.se.hub.modules.ai.dto.response.AiChatResponse;

public interface AiChatService {
    public AiChatResponse chat(AiChatRequest request);
}
