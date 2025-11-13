package com.se.hub.modules.profile.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.profile.constant.profile.ProfileConstants;
import com.se.hub.modules.profile.constant.profile.ProfileErrorCodeConstants;
import com.se.hub.modules.profile.enums.GenderEnums;
import com.se.hub.modules.user.constant.user.UserConstants;
import com.se.hub.modules.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = ProfileConstants.TABLE_PROFILE)
@Entity
public class Profile extends BaseEntity {

    @Column(name = ProfileConstants.FULL_NAME,
            columnDefinition = ProfileConstants.FULL_NAME_DEFINITION)
    @NotBlank(message = ProfileErrorCodeConstants.FULL_NAME_NOT_BLANK)
    @Size(min = ProfileConstants.FULL_NAME_MIN,
            max = ProfileConstants.FULL_NAME_MAX,
            message = ProfileErrorCodeConstants.FULL_NAME_SIZE_INVALID)
    String fullName;

    @Column(name = ProfileConstants.PHONE_NUM,
            unique = true,
            columnDefinition = ProfileConstants.PHONE_NUM_DEFINITION)
    @Pattern(regexp = ProfileConstants.PHONE_NUMBER_PATTERN,
            message = ProfileErrorCodeConstants.PHONE_NUMBER_PATTERN_INVALID)
    @Size(min = ProfileConstants.PHONE_NUM_MIN_LENGTH,
            max = ProfileConstants.PHONE_NUM_MAX_LENGTH,
            message = ProfileErrorCodeConstants.PHONE_NUMBER_PATTERN_INVALID)
    String phoneNum;

    @Column(name = ProfileConstants.EMAIL,
            unique = true,
            columnDefinition = ProfileConstants.EMAIL_DEFINITION)
    @Pattern(regexp = ProfileConstants.EMAIL_PATTERN,
            message = ProfileErrorCodeConstants.EMAIL_INVALID_FORMAT)
    @Size(max = ProfileConstants.EMAIL_MAX_LENGTH,
            message = ProfileErrorCodeConstants.EMAIL_SIZE_INVALID)
    String email;

    @Column(name = ProfileConstants.AVATAR_URL,
            columnDefinition = ProfileConstants.AVATAR_URL_DEFINITION)
    @Size(max = ProfileConstants.URL_MAX_LENGTH,
            message = ProfileErrorCodeConstants.AVATAR_URL_SIZE_INVALID)
    String avtUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = ProfileConstants.GENDER)
    @NotNull(message = ProfileErrorCodeConstants.GENDER_NOT_NULL)
    GenderEnums gender;

    @Column(name = ProfileConstants.IS_VERIFIED)
    boolean verified;

    @Column(name = ProfileConstants.IS_BLOCKED)
    boolean blocked;

    @Column(name = ProfileConstants.IS_ACTIVE)
    boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ProfileConstants.USER_LEVEL_ID,
            referencedColumnName = BaseFieldConstant.ID)
    @NotNull(message = ProfileErrorCodeConstants.LEVEL_NOT_NULL)
    UserLevel level;

    @OneToOne(mappedBy = "profile",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    UserStats userStats;

    @ManyToMany(fetch =  FetchType.LAZY)
    Set<Achievement> achievements;

    @OneToOne(fetch = FetchType.LAZY,
            optional = false)
    @JoinColumn(name = UserConstants.COL_USERID, referencedColumnName = BaseFieldConstant.ID)
    @NotNull(message = ProfileErrorCodeConstants.USER_NOT_NULL)
    User user;
}
