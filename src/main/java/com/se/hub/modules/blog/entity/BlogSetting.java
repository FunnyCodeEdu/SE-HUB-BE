package com.se.hub.modules.blog.entity;

import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.blog.constant.BlogConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Entity
@Table(name = BlogConstants.TABLE_BLOG_SETTING)
public class BlogSetting extends BaseEntity {

    @Builder.Default
    @Column(name = BlogConstants.COL_REQUIRE_APPROVAL,
            nullable = false)
    Boolean requireApproval = true; // Default: require approval

    // Singleton pattern - only one setting record exists
    public static final String SINGLETON_ID = "BLOG_SETTING_SINGLETON";
}

