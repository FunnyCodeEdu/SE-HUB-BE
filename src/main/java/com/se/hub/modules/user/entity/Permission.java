package com.se.hub.modules.user.entity;

import com.se.hub.modules.user.constant.permission.PermissionConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = PermissionConstants.TABLE_PERMISSION)
public class Permission {
    @Id
    @Column(name = PermissionConstants.COL_PERMISSION_NAME,
            nullable = false,
            columnDefinition = PermissionConstants.NAME_DEFINITION)
    String name;

    @Column(name = PermissionConstants.COL_DESCRIPTION,
            columnDefinition = PermissionConstants.DESCRIPTION_DEFINITION)
    String description;

    @ManyToMany(mappedBy = "permissions")
    Set<Role> roles;
}

