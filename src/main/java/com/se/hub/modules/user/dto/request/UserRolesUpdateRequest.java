package com.se.hub.modules.user.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRolesUpdateRequest {
    List<String> userRoles;

    public List<String> getUserRoles() {
        return this.userRoles.stream()
                .map(String::toUpperCase)
                .toList();
    }
}
