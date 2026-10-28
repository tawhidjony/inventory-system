package com.inventory.system.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Set;

@Data
public class RoleRequest {
    @NotBlank(message = "Role name is required")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Role name may contain only letters, digits and underscore")
    private String name; // যেমন: "MANAGER" (সেভ হওয়ার আগে uppercase করা হয়)

    private Set<String> permissions; // যেমন: ["READ_PRODUCT", "CREATE_SELL"]; আপডেটে null দিলে আগের পারমিশন থাকবে
}
