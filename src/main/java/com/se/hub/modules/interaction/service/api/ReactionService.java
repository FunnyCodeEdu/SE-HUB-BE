package com.se.hub.modules.interaction.service.api;

import com.se.hub.modules.interaction.dto.response.ReactionResponse;
import com.se.hub.modules.interaction.dto.response.ReactionToggleResult;
import com.se.hub.modules.interaction.enums.ReactionType;
import com.se.hub.modules.interaction.enums.TargetType;

public interface ReactionService {

    /**
     * Toggle reaction (like/unlike) for a target.
     * Returns true if reaction was added, false if removed.
     * @deprecated Use {@link #toggleReactionWithCount(TargetType, String, ReactionType)} instead
     */
    @Deprecated
    boolean toggleReaction(TargetType targetType, String targetId, ReactionType reactionType);

    /**
     * Toggle reaction (like/unlike) for a target and return both toggle result and count.
     * This is more efficient as it returns the count in the same operation.
     * @param targetType The target type (BLOG, COMMENT, etc.)
     * @param targetId The target ID
     * @param reactionType The reaction type (LIKE, etc.)
     * @return ReactionToggleResult containing isAdded flag and current count
     */
    ReactionToggleResult toggleReactionWithCount(TargetType targetType, String targetId, ReactionType reactionType);

    /**
     * Toggle reaction using string target type (converts to enum internally)
     * @param targetTypeString String representation of target type
     * @param targetId The target ID
     * @param reactionType The reaction type (LIKE, etc.)
     * @return ReactionResponse containing reaction status and count
     */
    ReactionResponse toggleReaction(String targetTypeString, String targetId, ReactionType reactionType);

    /**
     * Get reaction count for a target
     */
    long getReactionCount(TargetType targetType, String targetId, ReactionType reactionType);

    /**
     * Get reaction count using string target type (converts to enum internally)
     * @param targetTypeString String representation of target type
     * @param targetId The target ID
     * @param reactionType The reaction type (LIKE, etc.)
     * @return ReactionResponse containing count and user reaction status
     */
    ReactionResponse getReactionCount(String targetTypeString, String targetId, ReactionType reactionType);

    /**
     * Check if current user has reacted to target
     */
    boolean hasUserReacted(TargetType targetType, String targetId);
}

