package com.se.hub.modules.chat.repository;

import com.se.hub.modules.chat.entity.Conversation;
import com.se.hub.modules.chat.enums.ConversationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Conversation Repository
 * MongoDB repository for Conversation entities
 */
@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    /**
     * Find conversation by participants hash
     */
    Optional<Conversation> findByParticipantsHash(String participantsHash);
    
    /**
     * Find conversations by participant ID with pagination
     */
    @Query("{'participants.userId' : ?0}")
    Page<Conversation> findByParticipantIdsContains(String userId, Pageable pageable);
    
    /**
     * Find conversations by participant ID and type with pagination
     */
    @Query("{'participants.userId' : ?0, 'type' : ?1}")
    Page<Conversation> findByParticipantIdsContainsAndType(String userId, ConversationType type, Pageable pageable);
    
    /**
     * Check if conversation exists by participants hash
     */
    boolean existsByParticipantsHash(String participantsHash);
}

