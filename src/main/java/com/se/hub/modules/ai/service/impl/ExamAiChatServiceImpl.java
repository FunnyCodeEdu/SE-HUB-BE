package com.se.hub.modules.ai.service.impl;

import com.se.hub.modules.ai.dto.request.ExamAiChatRequest;
import com.se.hub.modules.ai.dto.response.AiChatResponse;
import com.se.hub.modules.ai.service.api.ExamAiChatService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
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
public class ExamAiChatServiceImpl implements ExamAiChatService {
    ChatClient chatClient;
    Map<String, List<org.springframework.ai.chat.messages.Message>> conversationHistory = new ConcurrentHashMap<>();

    public ExamAiChatServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public AiChatResponse chat(ExamAiChatRequest request) {
        String conversationId = request.getConversationId() == null ? "exam-default" : request.getConversationId();

        String safePrompt = request.getPrompt() == null ? "" : request.getPrompt();
        String safeQuestion = request.getQuestionText() == null ? "" : request.getQuestionText();

        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("You are an AI tutor helping a student discuss a single exam question.\n")
                .append("Keep responses concise (<=100 words), step-by-step when helpful.\n")
                .append("Focus only on the given question.\n\n")
                .append("Question:\n")
                .append(safeQuestion)
                .append("\n\nOptions:\n");
        if (request.getOptions() != null) {
            char label = 'A';
            for (String opt : request.getOptions()) {
                contextBuilder.append(label).append(") ").append(opt).append("\n");
                label++;
            }
        }

        SystemMessage systemMessage = new SystemMessage(contextBuilder.toString());

        List<org.springframework.ai.chat.messages.Message> history = conversationHistory
                .computeIfAbsent(conversationId, k -> new ArrayList<>());

        List<org.springframework.ai.chat.messages.Message> promptMessages = new ArrayList<>();
        promptMessages.add(systemMessage);
        if (!history.isEmpty()) {
            promptMessages.addAll(history);
        }
        UserMessage userMessage = new UserMessage(safePrompt);
        promptMessages.add(userMessage);
        Prompt prompt = new Prompt(promptMessages);

        String assistantContent;
        try {
            assistantContent = chatClient
                    .prompt(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("Exam AI chat error: {}", e.getMessage(), e);
            assistantContent = "Xin lỗi, tôi đang gặp sự cố khi xử lý câu hỏi này.";
        }

        history.add(userMessage);
        history.add(new AssistantMessage(assistantContent));
        if (history.size() > 40) {
            history.subList(0, history.size() - 40).clear();
        }

        return AiChatResponse.builder()
                .message(assistantContent)
                .build();
    }
}


