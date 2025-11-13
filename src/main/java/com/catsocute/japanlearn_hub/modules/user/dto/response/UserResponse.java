package com.catsocute.japanlearn_hub.modules.user.dto.response;

import com.catsocute.japanlearn_hub.modules.user.entity.Role;
import com.catsocute.japanlearn_hub.modules.user.enums.UserStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    UserStatus status;
    Set<Role> roles;
}
