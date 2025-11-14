package com.se.hub.modules.interaction.repository;

import com.se.hub.modules.interaction.entity.Reaction;
import com.se.hub.modules.interaction.enums.ReactionType;
import com.se.hub.modules.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, String> {

    /**
     * Find reaction by target and user
     */
    Optional<Reaction> findByTargetTypeAndTargetIdAndUser(
            com.se.hub.modules.interaction.enums.TargetType targetType,
            String targetId,
            Profile user);

    /**
     * Count reactions by target and reaction type
     */
    long countByTargetTypeAndTargetIdAndReactionType(
            com.se.hub.modules.interaction.enums.TargetType targetType,
            String targetId,
            ReactionType reactionType);

    /**
     * Delete reaction by target and user
     */
    void deleteByTargetTypeAndTargetIdAndUser(
            com.se.hub.modules.interaction.enums.TargetType targetType,
            String targetId,
            Profile user);

    /**
     * Check if reaction exists
     */
    boolean existsByTargetTypeAndTargetIdAndUser(
            com.se.hub.modules.interaction.enums.TargetType targetType,
            String targetId,
            Profile user);
}

