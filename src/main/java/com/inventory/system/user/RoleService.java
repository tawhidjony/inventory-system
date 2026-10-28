package com.inventory.system.user;

import com.inventory.system.common.exception.ResourceNotFoundException;
import com.inventory.system.user.dto.RolePermissionRequest;
import com.inventory.system.user.dto.RoleRequest;
import com.inventory.system.user.entity.Permission;
import com.inventory.system.user.entity.Role;
import com.inventory.system.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Role createRole(RoleRequest request) {
        String name = PermissionService.normalize(request.getName());
        roleRepository.findByName(name).ifPresent(existing -> {
            throw new RuntimeException("Role already exists: " + name);
        });

        Role role = new Role();
        role.setName(name);
        if (request.getPermissions() != null) {
            role.setPermissions(resolvePermissions(request.getPermissions()));
        }
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAllByOrderByNameAsc();
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }

    // নাম আপডেট; permissions দিলে পুরো পারমিশন সেট রিপ্লেস হয়
    @Transactional
    public Role updateRole(Long id, RoleRequest request) {
        Role role = getRoleById(id);
        ensureNotSuperAdmin(role);

        String name = PermissionService.normalize(request.getName());
        roleRepository.findByName(name)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new RuntimeException("Role already exists: " + name);
                });

        role.setName(name);
        if (request.getPermissions() != null) {
            role.setPermissions(resolvePermissions(request.getPermissions()));
        }
        return roleRepository.save(role);
    }

    // কোনো অ্যাকটিভ ইউজারের এই রোল থাকলে ডিলিট হবে না; সফট-ডিলিটেড ইউজারদের থেকে রোলটি সরিয়ে দেওয়া হয়
    @Transactional
    public void deleteRole(Long id) {
        Role role = getRoleById(id);
        ensureNotSuperAdmin(role);

        List<User> users = userRepository.findAllByRolesId(id);
        long activeUsers = users.stream().filter(user -> !user.isDeleted()).count();
        if (activeUsers > 0) {
            throw new RuntimeException("Role is assigned to " + activeUsers + " user(s); reassign them before deleting: " + role.getName());
        }
        for (User user : users) {
            user.getRoles().removeIf(r -> r.getId().equals(id));
            userRepository.save(user);
        }
        roleRepository.delete(role);
    }

    // রোলের পারমিশন সেট সম্পূর্ণ রিপ্লেস করা (খালি লিস্ট দিলে সব পারমিশন সরে যাবে)
    @Transactional
    public Role setPermissions(Long roleId, RolePermissionRequest request) {
        Role role = getRoleById(roleId);
        ensureNotSuperAdmin(role);
        role.setPermissions(resolvePermissions(request.getPermissions()));
        return roleRepository.save(role);
    }

    // বিদ্যমান পারমিশনের সাথে নতুন পারমিশন যোগ করা
    @Transactional
    public Role addPermissions(Long roleId, RolePermissionRequest request) {
        Role role = getRoleById(roleId);
        ensureNotSuperAdmin(role);
        role.getPermissions().addAll(resolvePermissions(request.getPermissions()));
        return roleRepository.save(role);
    }

    @Transactional
    public Role removePermission(Long roleId, String permissionName) {
        Role role = getRoleById(roleId);
        ensureNotSuperAdmin(role);

        String name = PermissionService.normalize(permissionName);
        if (!role.getPermissions().removeIf(p -> p.getName().equals(name))) {
            throw new ResourceNotFoundException("Role " + role.getName() + " does not have permission: " + name);
        }
        return roleRepository.save(role);
    }

    // SUPER_ADMIN সবসময় সব পারমিশন পায় (DataSeeder প্রতি স্টার্টআপে রিসেট করে), তাই API দিয়ে পরিবর্তন নিষেধ
    private void ensureNotSuperAdmin(Role role) {
        if (DataSeeder.SUPER_ADMIN_ROLE.equals(role.getName())) {
            throw new RuntimeException("SUPER_ADMIN role cannot be modified or deleted");
        }
    }

    private Set<Permission> resolvePermissions(Set<String> permissionNames) {
        Set<Permission> permissions = new HashSet<>();
        for (String permissionName : permissionNames) {
            String name = PermissionService.normalize(permissionName);
            Permission permission = permissionRepository.findByName(name)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission not found: " + name));
            permissions.add(permission);
        }
        return permissions;
    }
}
