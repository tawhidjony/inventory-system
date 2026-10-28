package com.inventory.system.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class RolePermissionRequest {
    @NotNull(message = "Permissions are required")
    private Set<String> permissions; // যেমন: ["READ_PRODUCT", "WRITE_PRODUCT"]
}
