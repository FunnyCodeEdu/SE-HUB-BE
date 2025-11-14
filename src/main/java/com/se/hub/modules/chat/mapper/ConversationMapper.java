package com.se.hub.modules.chat.mapper;

import com.se.hub.modules.chat.dto.request.CreateConversationRequest;
import com.se.hub.modules.chat.dto.response.ConversationResponse;
import com.se.hub.modules.chat.dto.response.ParticipantInfoResponse;
import com.se.hub.modules.chat.entity.Conversation;
import com.se.hub.modules.chat.entity.ParticipantInfo;
import com.se.hub.modules.chat.enums.ConversationType;
import com.se.hub.modules.profile.repository.ProfileRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Conversation Mapper
 * MapStruct mapper for Conversation entity and DTOs
 * Fetches Profile info for participants
 */
@Mapper(componentModel = "spring")
public abstract class ConversationMapper {
    
    @Autowired
    ProfileRepository profileRepository;
    
    /**
     * Map CreateConversationRequest -> Conversation entity
     * Note: participantsHash and participants will be set in service layer
     */
    @Mapping(target = "conversationId", ignore = true)
    @Mapping(target = "participantsHash", ignore = true)
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    public abstract Conversation toConversation(CreateConversationRequest request);
    
    /**
     * Map Conversation entity -> ConversationResponse
     * Note: participants, conversationName will be fetched from Profile, lastMessage and unreadCount will be set in service layer
     */
    @Mapping(target = "participants", expression = "java(fetchParticipantsInfo(conversation.getParticipants()))")
    @Mapping(target = "conversationName", expression = "java(getConversationName(conversation, currentUserId))")
    @Mapping(target = "lastMessage", ignore = true)
    @Mapping(target = "unreadCount", ignore = true)
    public abstract ConversationResponse toConversationResponse(Conversation conversation, String currentUserId);
    
    /**
     * Default method for backward compatibility (without currentUserId)
     */
    public ConversationResponse toConversationResponse(Conversation conversation) {
        return toConversationResponse(conversation, null);
    }
    
    /**
     * Fetch participants info from Profile (batch fetch)
     */
    protected List<ParticipantInfoResponse> fetchParticipantsInfo(List<ParticipantInfo> participants) {
        if (participants == null || participants.isEmpty()) {
            return List.of();
        }
        
        return participants.stream()
            .map(participant -> {
                String userId = participant.getUserId();
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
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get conversation name based on type
     * For DIRECT: return other participant name
     * For GROUP: return group name or first few participant names
     */
    protected String getConversationName(Conversation conversation, String currentUserId) {
        if (conversation == null || conversation.getParticipants() == null) {
            return "Unknown Conversation";
        }
        
        List<ParticipantInfoResponse> participants = fetchParticipantsInfo(conversation.getParticipants());
        
        if (conversation.getType() == ConversationType.DIRECT) {
            // For DIRECT: return other participant name
            return participants.stream()
                .filter(p -> currentUserId == null || !p.getUserId().equals(currentUserId))
                .findFirst()
                .map(ParticipantInfoResponse::getFullName)
                .orElse("Unknown User");
        } else {
            // For GROUP: return first few participant names or "Group Chat"
            if (participants.size() <= 3) {
                return participants.stream()
                    .map(ParticipantInfoResponse::getFullName)
                    .collect(Collectors.joining(", "));
            } else {
                return participants.stream()
                    .limit(2)
                    .map(ParticipantInfoResponse::getFullName)
                    .collect(Collectors.joining(", ")) + " and " + (participants.size() - 2) + " others";
            }
        }
    }
}

