package com.se.hub.modules.chat.mapper;

import com.se.hub.modules.chat.dto.request.CreateChatMessageRequest;
import com.se.hub.modules.chat.dto.response.ChatMessageResponse;
import com.se.hub.modules.chat.dto.response.ParticipantInfoResponse;
import com.se.hub.modules.chat.entity.ChatMessage;
import com.se.hub.modules.profile.repository.ProfileRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Chat Message Mapper
 * MapStruct mapper for ChatMessage entity and DTOs
 * Fetches Profile info for sender
 */
@Mapper(componentModel = "spring")
public abstract class ChatMessageMapper {
    
    @Autowired
    ProfileRepository profileRepository;
    
    /**
     * Map CreateChatMessageRequest -> ChatMessage entity
     * Note: senderId will be set in service layer
     */
    @Mapping(target = "messageId", ignore = true)
    @Mapping(target = "senderId", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    public abstract ChatMessage toChatMessage(CreateChatMessageRequest request);
    
    /**
     * Map ChatMessage entity -> ChatMessageResponse
     * Note: sender will be fetched from Profile, isMe will be set in service layer
     */
    @Mapping(target = "sender", expression = "java(fetchParticipantInfo(message.getSenderId()))")
    @Mapping(target = "isMe", ignore = true)
    public abstract ChatMessageResponse toChatMessageResponse(ChatMessage message);
    
    /**
     * Fetch participant info from Profile
     */
    @Named("fetchParticipantInfo")
    protected ParticipantInfoResponse fetchParticipantInfo(String userId) {
        if (userId == null) {
            return null;
        }
        
        return profileRepository.findByUserId(userId)
            .map(profile -> ParticipantInfoResponse.builder()
                .userId(profile.getUser().getId())
                .username(profile.getUsername())
                .fullName(profile.getFullName())
                .avatarUrl(profile.getAvtUrl())
                .build())
            .orElse(ParticipantInfoResponse.builder()
                .userId(userId)
                .username("Unknown")
                .fullName("Unknown User")
                .build());
    }
}

