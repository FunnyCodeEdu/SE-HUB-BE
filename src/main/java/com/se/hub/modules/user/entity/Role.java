package com.se.hub.modules.user.entity;

import com.se.hub.modules.user.constant.role.RoleConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = RoleConstants.TABLE_ROLE)
public class Role {
    @Id
    @Column(name = RoleConstants.COL_ROLE_NAME,
            nullable = false,
            columnDefinition = RoleConstants.NAME_DEFINITION)
    @Size(min = RoleConstants.MIN_CHARS_ROLE_NAME,
            max = RoleConstants.MAX_CHARS_ROLE_NAME)
    String name;

    @Column(name = RoleConstants.COL_DESCRIPTION,
            columnDefinition = RoleConstants.DESCRIPTION_DEFINITION)
    @Size(min = RoleConstants.MIN_CHARS_DESCRIPTION,
            max =  RoleConstants.MAX_CHARS_DESCRIPTION)
    String description;

    @ManyToMany(fetch = FetchType.LAZY)
    Set<Permission> permissions;
}
