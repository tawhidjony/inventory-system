package com.inventory.system.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // সব ইউজার দেখতে (শুধুমাত্র সুপার এডমিন বা MANAGE_USERS পারমিশন হোল্ডার)
    @GetMapping
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'MANAGE_USERS')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // অন্য ইউজারের রোল/পারমিশন পরিবর্তন করতে (সুপার এডমিন কন্ট্রোল)
    @PutMapping("/{id}/roles")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'MANAGE_USERS')")
    public ResponseEntity<User> updateUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest request) {
        User updatedUser = userService.updateUserRoles(id, request);
        return ResponseEntity.ok(updatedUser);
    }
}