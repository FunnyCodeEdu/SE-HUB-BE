package com.se.hub.modules.user.entity;

import com.se.hub.modules.user.constant.role.RoleConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    String name;
}
