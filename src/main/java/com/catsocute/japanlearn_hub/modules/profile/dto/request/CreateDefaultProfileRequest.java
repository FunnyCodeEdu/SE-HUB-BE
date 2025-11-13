package com.catsocute.japanlearn_hub.modules.profile.dto.request;

import com.catsocute.japanlearn_hub.modules.user.dto.request.UserCreationRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateDefaultProfileRequest {
    UserCreationRequest request;
}
