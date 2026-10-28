package com.inventory.system.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.Set;

@Data
public class UpdateUserRoleRequest {
    @NotEmpty(message = "Roles cannot be empty")
    private Set<String> roles; // যেমন: ["MANAGER", "STAFF"]
}