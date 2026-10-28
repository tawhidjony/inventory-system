package com.inventory.system.user;

import com.inventory.system.common.exception.ResourceNotFoundException;
import com.inventory.system.user.dto.PermissionRequest;
import com.inventory.system.user.entity.Permission;
import com.inventory.system.user.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public Permission createPermission(PermissionRequest request) {
        String name = normalize(request.getName());
        permissionRepository.findByName(name).ifPresent(existing -> {
            throw new RuntimeException("Permission already exists: " + name);
        });

        Permission permission = new Permission();
        permission.setName(name);
        return permissionRepository.save(permission);
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAllByOrderByNameAsc();
    }

    public Permission getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));
    }

    @Transactional
    public Permission updatePermission(Long id, PermissionRequest request) {
        Permission permission = getPermissionById(id);
        ensureNotSystemPermission(permission);

        String name = normalize(request.getName());
        permissionRepository.findByName(name)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new RuntimeException("Permission already exists: " + name);
                });

        permission.setName(name);
        return permissionRepository.save(permission);
    }

    // ডিলিটের আগে যেসব রোলে এই পারমিশন আছে সেগুলো থেকে সরিয়ে দেওয়া হয় (role_permissions FK এর জন্য)
    @Transactional
    public void deletePermission(Long id) {
        Permission permission = getPermissionById(id);
        ensureNotSystemPermission(permission);

        for (Role role : roleRepository.findAllByPermissionsId(id)) {
            role.getPermissions().removeIf(p -> p.getId().equals(id));
            roleRepository.save(role);
        }
        permissionRepository.delete(permission);
    }

    static String normalize(String name) {
        return name.trim().toUpperCase(Locale.ROOT);
    }

    // কোডে ব্যবহৃত পারমিশন rename/delete করলে endpoint গুলো লক হয়ে যাবে, আর DataSeeder আবার তৈরি করে ফেলবে
    private void ensureNotSystemPermission(Permission permission) {
        if (DataSeeder.PERMISSIONS.contains(permission.getName())) {
            throw new RuntimeException("System permission cannot be modified or deleted: " + permission.getName());
        }
    }
}
