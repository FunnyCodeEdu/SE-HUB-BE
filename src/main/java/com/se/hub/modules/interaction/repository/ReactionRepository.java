package com.se.hub.modules.interaction.repository;

import com.se.hub.modules.interaction.entity.Reaction;
import com.se.hub.modules.interaction.enums.ReactionType;
import com.se.hub.modules.interaction.enums.TargetType;
import com.se.hub.modules.profile.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    /**
     * Find all reactions by target type, target IDs and user (batch check)
     */
    @Query("SELECT r FROM Reaction r WHERE r.targetType = :targetType AND r.targetId IN :targetIds AND r.user = :user")
    List<Reaction> findByTargetTypeAndTargetIdInAndUser(
            @Param("targetType") TargetType targetType,
            @Param("targetIds") List<String> targetIds,
            @Param("user") Profile user);

    /**
     * Batch count reactions by target type, target IDs and reaction type
     * Returns map of targetId -> count grouped by reaction type
     */
    @Query("SELECT r.targetId, r.reactionType, COUNT(r) FROM Reaction r " +
           "WHERE r.targetType = :targetType AND r.targetId IN :targetIds " +
           "GROUP BY r.targetId, r.reactionType")
    List<Object[]> countByTargetTypeAndTargetIdInGroupByReactionType(
            @Param("targetType") TargetType targetType,
            @Param("targetIds") List<String> targetIds);

    /**
     * Find all reactions with pagination
     * Uses LEFT JOIN FETCH to optimize N+1 problem by eagerly loading user
     */
    @Query(value = "SELECT DISTINCT r FROM Reaction r LEFT JOIN FETCH r.user",
           countQuery = "SELECT COUNT(DISTINCT r.id) FROM Reaction r")
    Page<Reaction> findAllWithUser(Pageable pageable);

    /**
     * Find all reactions by target type with pagination
     * Uses LEFT JOIN FETCH to optimize N+1 problem by eagerly loading user
     */
    @Query(value = "SELECT DISTINCT r FROM Reaction r LEFT JOIN FETCH r.user WHERE r.targetType = :targetType",
           countQuery = "SELECT COUNT(DISTINCT r.id) FROM Reaction r WHERE r.targetType = :targetType")
    Page<Reaction> findAllByTargetTypeWithUser(@Param("targetType") TargetType targetType, Pageable pageable);
}

