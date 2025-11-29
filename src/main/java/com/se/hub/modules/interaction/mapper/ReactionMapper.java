package com.se.hub.modules.interaction.mapper;

import com.se.hub.modules.interaction.dto.response.ReactionDetailResponse;
import com.se.hub.modules.interaction.entity.Reaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReactionMapper {

    /**
     * Map Reaction entity -> ReactionDetailResponse
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user", target = "userName", qualifiedByName = "mapUserName")
    @Mapping(source = "user.avtUrl", target = "userAvatar")
    ReactionDetailResponse toReactionDetailResponse(Reaction reaction);

    /**
     * Map List<Reaction> -> List<ReactionDetailResponse>
     */
    List<ReactionDetailResponse> toReactionDetailResponseList(List<Reaction> reactions);

    /**
     * Map user to userName - use username if available, otherwise use fullName
     */
    @Named("mapUserName")
    default String mapUserName(com.se.hub.modules.profile.entity.Profile user) {
        if (user == null) {
            return null;
        }
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            return user.getUsername();
        }
        return user.getFullName();
    }
}

