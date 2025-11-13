package com.se.hub.modules.user.entity;

import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.user.constant.user.UserConstants;
import com.se.hub.modules.user.enums.UserStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = UserConstants.TABLE_USER)
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // ID from JWT token (no @GeneratedValue - set manually)
    @Id
    @Column(name = "id", updatable = false)
    String id;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    String createdBy;

    @LastModifiedBy
    @Column(name = "update_by")
    String updateBy;

    @Column(name = "create_date", updatable = false)
    Instant createDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    Instant updatedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = UserConstants.COL_STATUS,
            nullable = false)
    UserStatus status;

    @ManyToOne
    @JoinColumn(name = "role_name")
    Role role;

    @OneToOne(mappedBy = "user")
    Profile profile;

    @PrePersist
    protected void onCreate() {
        if (this.createDate == null) {
            this.createDate = Instant.now();
        }
        if (this.updatedDate == null) {
            this.updatedDate = Instant.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = Instant.now();
    }
}
