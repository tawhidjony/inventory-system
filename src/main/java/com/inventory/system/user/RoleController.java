package com.inventory.system.user;

import com.inventory.system.user.dto.RolePermissionRequest;
import com.inventory.system.user.dto.RoleRequest;
import com.inventory.system.user.entity.Role;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'MANAGE_ROLES')")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<Role> createRole(@Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.createRole(request));
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    // রোলের পারমিশন সম্পূর্ণ রিপ্লেস করা
    @PutMapping("/{id}/permissions")
    public ResponseEntity<Role> setPermissions(@PathVariable Long id, @Valid @RequestBody RolePermissionRequest request) {
        return ResponseEntity.ok(roleService.setPermissions(id, request));
    }

    // রোলে নতুন পারমিশন অ্যাসাইন করা (আগেরগুলো থাকবে)
    @PostMapping("/{id}/permissions")
    public ResponseEntity<Role> addPermissions(@PathVariable Long id, @Valid @RequestBody RolePermissionRequest request) {
        return ResponseEntity.ok(roleService.addPermissions(id, request));
    }

    // রোল থেকে একটি পারমিশন সরানো
    @DeleteMapping("/{id}/permissions/{permissionName}")
    public ResponseEntity<Role> removePermission(@PathVariable Long id, @PathVariable String permissionName) {
        return ResponseEntity.ok(roleService.removePermission(id, permissionName));
    }
}
