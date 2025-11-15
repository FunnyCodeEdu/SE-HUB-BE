package com.se.hub.modules.blog.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.blog.constant.BlogConstants;
import com.se.hub.modules.blog.constant.BlogErrorCodeConstants;
import com.se.hub.modules.profile.entity.Profile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = BlogConstants.TABLE_BLOG)
@Entity
public class Blog extends BaseEntity {
    @NotNull(message = BlogErrorCodeConstants.BLOG_AUTHOR_INVALID)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = BlogConstants.COL_AUTHOR_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Profile author;

    @NotBlank(message = BlogErrorCodeConstants.BLOG_CONTENT_INVALID)
    @Size(max = BlogConstants.CONTENT_MAX_LENGTH,
            message = BlogErrorCodeConstants.BLOG_CONTENT_INVALID)
    @Column(name = BlogConstants.COL_CONTENT,
            nullable = false,
            columnDefinition = BlogConstants.CONTENT_DEFINITION)
    String content;

    @Column(name = BlogConstants.COL_COVER_IMAGE_URL,
            columnDefinition = BlogConstants.IMG_URL_DEFINITION)
    String coverImageUrl;

    @Builder.Default
    @Column(name = BlogConstants.COL_COMMENT_COUNT,
            nullable = false)
    int cmtCount = 0;

    @Builder.Default
    @Column(name = BlogConstants.COL_REACTION_COUNT,
            nullable = false)
    int reactionCount = 0;

    @Builder.Default
    @Column(name = BlogConstants.COL_VIEW_COUNT,
            nullable = false)
    int viewCount = 0;

    @NotNull(message = BlogErrorCodeConstants.BLOG_ALLOW_COMMENTS_INVALID)
    @Column(name = BlogConstants.COL_ALLOW_COMMENTS,
            nullable = false)
    Boolean allowComments;

    @Builder.Default
    @Column(name = BlogConstants.COL_IS_APPROVED,
            nullable = false)
    Boolean isApproved = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Blog blog = (Blog) o;
        return getId() != null && getId().equals(blog.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
