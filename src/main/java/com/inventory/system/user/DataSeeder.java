package com.inventory.system.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {
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
        if (permissionRepository.count() == 0) {
            Permission p1 = new Permission(); p1.setName("READ_PRODUCT");
            Permission p2 = new Permission(); p2.setName("WRITE_PRODUCT");
            Permission p3 = new Permission(); p3.setName("DELETE_PRODUCT");
            Permission p4 = new Permission(); p4.setName("MANAGE_USERS");
            Permission p5 = new Permission(); p5.setName("MANAGE_STOCK");
            Permission p6 = new Permission(); p6.setName("CREATE_SELL");
            Permission p7 = new Permission(); p7.setName("READ_SELL");

            permissionRepository.saveAll(Set.of(p1, p2, p3, p4, p5, p6, p7));
        }

        Role superAdminRole = roleRepository.findByName("SUPER_ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setName("SUPER_ADMIN");
            role.setPermissions(new HashSet<>(permissionRepository.findAll())); // সব পারমিশন সুপার এডমিনের
            return roleRepository.save(role);
        });

        // ৩. ডিফল্ট সুপার এডমিন ইউজার তৈরি (যদি না থাকে)
        if (userRepository.findByEmail("admin@inventory.com").isEmpty()) {
            User admin = new User();
            admin.setName("Super Admin");
            admin.setEmail("admin@inventory.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // পাসওয়ার্ড এনক্রিপ্ট করা
            admin.setActive(true);
            admin.setRoles(Set.of(superAdminRole));

            userRepository.save(admin);
            System.out.println(">>> Default Super Admin Created: admin@inventory.com / admin123");
        }
    }

}
