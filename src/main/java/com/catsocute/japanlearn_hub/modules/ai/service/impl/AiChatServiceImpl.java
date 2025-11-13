package com.catsocute.japanlearn_hub.modules.ai.service.impl;

import com.catsocute.japanlearn_hub.modules.ai.dto.request.AiChatRequest;
import com.catsocute.japanlearn_hub.modules.ai.dto.response.AiChatResponse;
import com.catsocute.japanlearn_hub.modules.ai.service.api.AiChatService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AiChatServiceImpl implements AiChatService {
    ChatClient chatClient;
    Map<String, List<org.springframework.ai.chat.messages.Message>> conversationHistory = new ConcurrentHashMap<>();

    public AiChatServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public AiChatResponse chat(AiChatRequest request) {
        String conversationId = request.getConversationId();
        SystemMessage systemMessage = new SystemMessage("""
                You are AI Assistant in JLearnHub
                You are a Japanese language learning support assistant.
                You can only answer questions related to Japanese language, roadmap, and academic assessment.
                In addition, you are not allowed to answer any questions about other topics.
                Give a concise answer, no longer than 60 words. Focus only on the key point.
                """);

        List<org.springframework.ai.chat.messages.Message> history = conversationHistory
                .computeIfAbsent(conversationId == null ? "default" : conversationId, k -> new ArrayList<>());

        // Build prompt with system message, history, and new user message
        List<org.springframework.ai.chat.messages.Message> promptMessages = new ArrayList<>();
        promptMessages.add(systemMessage);
        if (!history.isEmpty()) {
            promptMessages.addAll(history);
        }
        UserMessage userMessage = new UserMessage(request.getPrompt());
        promptMessages.add(userMessage);
        Prompt prompt = new Prompt(promptMessages);

        String assistantContent = chatClient
                .prompt(prompt)
                .call()
                .content();

        // Update history with the latest user and assistant messages; cap to last 20 messages
        history.add(userMessage);
        assert assistantContent != null;
        history.add(new AssistantMessage(assistantContent));
        if (history.size() > 40) { // 20 exchanges * 2
            history.subList(0, history.size() - 40).clear();
        }

        return AiChatResponse.builder()
                .message(assistantContent)
                .build();
    }
}
