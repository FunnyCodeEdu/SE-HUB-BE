package com.se.hub.modules.user.entity;

import com.se.hub.modules.user.constant.permission.PermissionConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = PermissionConstants.TABLE_PERMISSION)
public class Permission {
    @Id
    @NotBlank
    @Column(name = PermissionConstants.COL_PERMISSION_NAME,
            nullable = false,
            columnDefinition =  PermissionConstants.NAME_DEFINITION)
    @Size(min = PermissionConstants.MIN_CHARS_PERMISSION_NAME,
            max = PermissionConstants.MAX_CHARS_PERMISSION_NAME)
    String name;

    @Column(name = PermissionConstants.COL_DESCRIPTION,
            columnDefinition =  PermissionConstants.DESCRIPTION_DEFINITION)
    @Size(min = PermissionConstants.MIN_CHARS_DESCRIPTION,
            max = PermissionConstants.MAX_CHARS_DESCRIPTION)
    String description;
}
