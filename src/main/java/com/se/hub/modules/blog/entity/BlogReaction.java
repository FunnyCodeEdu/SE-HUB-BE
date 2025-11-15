package com.se.hub.modules.blog.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.blog.constant.BlogConstants;
import com.se.hub.modules.profile.entity.Profile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = BlogConstants.TABLE_BLOG_REACTION,
        uniqueConstraints = {
                @UniqueConstraint(
                        name = BlogConstants.UNIQUE_BLOG_REACTION_USER_BLOG,
                        columnNames = {BlogConstants.COL_USER_ID, BlogConstants.COL_BLOG_ID}
                )
        })
@Entity
public class BlogReaction extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = BlogConstants.COL_BLOG_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Blog blog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = BlogConstants.COL_USER_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Profile user;

    @Column(name = BlogConstants.COL_IS_LIKE,
            nullable = false)
    Boolean isLike = true;
}

