package com.se.hub.modules.user.entity;

import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.user.constant.user.UserConstants;
import com.se.hub.modules.user.constant.user.UserErrorCodeConstants;
import com.se.hub.modules.user.enums.UserStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
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
@Entity
@Table(name = UserConstants.TABLE_USER)
public class User extends BaseEntity {
    @Column(name = UserConstants.COL_USERNAME,
            unique = true,
            nullable = false,
            columnDefinition = UserConstants.USERNAME_DEFINITION)
    @Size(min = UserConstants.MIN_CHARS_USERNAME,
            max = UserConstants.MAX_CHARS_USERNAME,
            message = UserErrorCodeConstants.USER_USERNAME_INVALID)
    String username;

    @Column(name = UserConstants.COL_PASSWORD,
            nullable = false,
            columnDefinition = UserConstants.PASSWORD_DEFINITION)
    @Size(min = UserConstants.MIN_CHARS_PASSWORD,
            max = UserConstants.MAX_CHARS_PASSWORD,
            message = UserErrorCodeConstants.USER_PASSWORD_INVALID)
    String password;

    @Enumerated(EnumType.STRING)
    @Column(name = UserConstants.COL_STATUS,
            nullable = false)
    UserStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    Set<Role> roles;

    @OneToOne(mappedBy = "user",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    Profile  profile;
}
