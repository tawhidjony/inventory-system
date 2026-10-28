package com.inventory.system.user;

import com.inventory.system.user.entity.Permission;
import com.inventory.system.user.entity.Role;
import com.inventory.system.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final List<String> PERMISSIONS = List.of(
            "READ_PRODUCT", "WRITE_PRODUCT", "DELETE_PRODUCT",
            "MANAGE_USERS", "MANAGE_STOCK", "MANAGE_CUSTOMER",
            "CREATE_SELL", "READ_SELL"
    );

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // ১. যে পারমিশনগুলো নেই সেগুলো তৈরি করা (নতুন পারমিশন যোগ হলে পুরনো DB তেও চলে আসবে)
        for (String name : PERMISSIONS) {
            permissionRepository.findByName(name).orElseGet(() -> {
                Permission permission = new Permission();
                permission.setName(name);
                return permissionRepository.save(permission);
            });
        }

        // ২. সুপার এডমিন রোল: সব পারমিশন সবসময় আপ-টু-ডেট থাকে
        Role superAdminRole = roleRepository.findByName("SUPER_ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setName("SUPER_ADMIN");
            return role;
        });
        superAdminRole.setPermissions(new HashSet<>(permissionRepository.findAll()));
        superAdminRole = roleRepository.save(superAdminRole);

        // ৩. ডিফল্ট সুপার এডমিন ইউজার তৈরি (যদি না থাকে)
        if (userRepository.findByEmail("admin@inventory.com").isEmpty()) {
            User admin = new User();
            admin.setName("Super Admin");
            admin.setEmail("admin@inventory.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // পাসওয়ার্ড এনক্রিপ্ট করা
            admin.setActive(true);
            admin.setRoles(Set.of(superAdminRole));

            userRepository.save(admin);
            System.out.println(">>> Default Super Admin Created: admin@inventory.com / admin123");
        }
    }

}
