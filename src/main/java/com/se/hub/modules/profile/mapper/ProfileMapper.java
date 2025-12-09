package com.se.hub.modules.profile.mapper;

import com.se.hub.modules.gamification.mapper.GamificationProfileMapper;
import com.se.hub.modules.profile.dto.request.UpdateProfileRequest;
import com.se.hub.modules.profile.dto.response.ProfileResponse;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.user.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {GamificationProfileMapper.class, UserStatsMapper.class, AchievementMapper.class})
public interface ProfileMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.role", target = "userRole", qualifiedByName = "mapRoleToSet")
    @Mapping(source = "user.status", target = "userStatus")
    @Mapping(target = "verified", expression = "java(profile.isVerified())")
    @Mapping(target = "blocked", expression = "java(profile.isBlocked())")
    @Mapping(target = "active", expression = "java(profile.isActive())")
    @Mapping(target = "contributionGraph", ignore = true)
    ProfileResponse toProfileResponse(Profile profile);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.role", target = "userRole", qualifiedByName = "mapRoleToSet")
    @Mapping(source = "user.status", target = "userStatus")
    @Mapping(target = "verified", expression = "java(profile.isVerified())")
    @Mapping(target = "blocked", expression = "java(profile.isBlocked())")
    @Mapping(target = "active", expression = "java(profile.isActive())")
    @Mapping(target = "contributionGraph", ignore = true)
    List<ProfileResponse> toListProfileResponse(List<Profile> profiles);

    void updateProfileFromRequest(@MappingTarget Profile profile, UpdateProfileRequest request);

    @Named("mapRoleToSet")
    default Set<String> mapRoleToSet(Role role) {
        if (role == null) return null;
        return Set.of(role.getName());
    }
}
