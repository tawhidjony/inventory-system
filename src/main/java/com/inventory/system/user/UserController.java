package com.inventory.system.user;

import com.inventory.system.user.dto.CreateUserRequest;
import com.inventory.system.user.dto.UpdateUserRequest;
import com.inventory.system.user.dto.UpdateUserRoleRequest;
import com.inventory.system.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'MANAGE_USERS')")
public class UserController {

    @Autowired
    private UserService userService;

    // নতুন ইউজার তৈরি (শুধুমাত্র সুপার এডমিন বা MANAGE_USERS পারমিশন হোল্ডার)
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    // সব ইউজার দেখতে
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    // অন্য ইউজারের রোল/পারমিশন পরিবর্তন করতে (সুপার এডমিন কন্ট্রোল)
    @PutMapping("/{id}/roles")
    public ResponseEntity<User> updateUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest request) {
        return ResponseEntity.ok(userService.updateUserRoles(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
