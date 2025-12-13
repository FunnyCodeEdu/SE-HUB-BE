package com.se.hub.modules.profile.entity;

import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.profile.constant.follow.FollowConstants;
import com.se.hub.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = FollowConstants.TABLE_PRIVACY_SETTING,
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {FollowConstants.COL_FOLLOWER_ID, FollowConstants.COL_FOLLOWING_ID})
    },
    indexes = {
        @Index(name = FollowConstants.INDEX_FOLLOWER, columnList = FollowConstants.COL_FOLLOWER_ID),
        @Index(name = FollowConstants.INDEX_FOLLOWING, columnList = FollowConstants.COL_FOLLOWING_ID)
    }
)
public class Follow extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = FollowConstants.COL_FOLLOWER_ID, nullable = false)
    User follower; // Người follow

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = FollowConstants.COL_FOLLOWING_ID, nullable = false)
    User following; // Người được follow
}

