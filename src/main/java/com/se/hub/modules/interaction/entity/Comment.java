package com.se.hub.modules.interaction.entity;

import com.se.hub.common.constant.BaseFieldConstant;
import com.se.hub.common.entity.BaseEntity;
import com.se.hub.modules.interaction.constant.CommentConstants;
import com.se.hub.modules.interaction.constant.CommentErrorCodeConstants;
import com.se.hub.modules.interaction.enums.TargetType;
import com.se.hub.modules.profile.entity.Profile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = CommentConstants.TABLE_COMMENT)
@Entity
public class Comment extends BaseEntity {

    @NotNull(message = CommentErrorCodeConstants.COMMENT_AUTHOR_INVALID)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = CommentConstants.COL_AUTHOR_ID,
            referencedColumnName = BaseFieldConstant.ID,
            nullable = false)
    Profile author;

    @NotNull(message = CommentErrorCodeConstants.COMMENT_TARGET_TYPE_INVALID)
    @Enumerated(EnumType.STRING)
    @Column(name = CommentConstants.COL_TARGET_TYPE,
            nullable = false,
            columnDefinition = CommentConstants.TARGET_TYPE_DEFINITION)
    TargetType targetType;

    @NotBlank(message = CommentErrorCodeConstants.COMMENT_TARGET_ID_INVALID)
    @NotNull(message = CommentErrorCodeConstants.COMMENT_TARGET_ID_INVALID)
    @Size(max = CommentConstants.TARGET_ID_MAX_LENGTH,
            message = CommentErrorCodeConstants.COMMENT_TARGET_ID_INVALID)
    @Column(name = CommentConstants.COL_TARGET_ID,
            nullable = false,
            columnDefinition = CommentConstants.TARGET_ID_DEFINITION)
    String targetId;

    @NotBlank(message = CommentErrorCodeConstants.COMMENT_CONTENT_INVALID)
    @NotNull(message = CommentErrorCodeConstants.COMMENT_CONTENT_INVALID)
    @Size(min = CommentConstants.CONTENT_MIN_LENGTH,
            max = CommentConstants.CONTENT_MAX_LENGTH,
            message = CommentErrorCodeConstants.COMMENT_CONTENT_INVALID)
    @Column(name = CommentConstants.COL_CONTENT,
            nullable = false,
            columnDefinition = CommentConstants.CONTENT_DEFINITION)
    String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = CommentConstants.COL_PARENT_COMMENT,
            referencedColumnName = BaseFieldConstant.ID)
    Comment parentComment;

    @OneToMany(mappedBy = "parentComment",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    List<Comment> replies;
}

