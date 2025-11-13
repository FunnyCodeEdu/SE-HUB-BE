package com.se.hub.modules.profile.mapper;

import com.se.hub.modules.profile.dto.request.UpdateProfileRequest;
import com.se.hub.modules.profile.dto.response.ProfileResponse;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.user.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.roles", target = "userRole")
    @Mapping(source = "user.status", target = "userStatus")
    ProfileResponse toProfileResponse(Profile profile);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.roles", target = "userRole")
    @Mapping(source = "user.status", target = "userStatus")
    List<ProfileResponse> toListProfileResponse(List<Profile> profiles);

    void updateProfileFromRequest(@MappingTarget Profile profile, UpdateProfileRequest request);

    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
