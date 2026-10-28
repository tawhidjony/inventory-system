package com.inventory.system.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PermissionRequest {
    @NotBlank(message = "Permission name is required")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Permission name may contain only letters, digits and underscore")
    private String name; // যেমন: "READ_REPORT" (সেভ হওয়ার আগে uppercase করা হয়)
}
