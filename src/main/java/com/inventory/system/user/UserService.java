package com.inventory.system.user;

import com.inventory.system.user.dto.UpdateUserRoleRequest;
import com.inventory.system.user.entity.Role;
import com.inventory.system.user.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // সব ইউজারের তালিকা দেখা
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // সুপার এডমিন কর্তৃক অন্য ইউজারের রোল বা পারমিশন আপডেট করা
    @Transactional
    public User updateUserRoles(Long userId, UpdateUserRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Set<Role> newRoles = new HashSet<>();
        for (String roleName : request.getRoles()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            newRoles.add(role);
        }

        user.setRoles(newRoles);
        return userRepository.save(user);
    }
}