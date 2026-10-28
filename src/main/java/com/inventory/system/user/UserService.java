package com.inventory.system.user;

import com.inventory.system.common.exception.ResourceNotFoundException;
import com.inventory.system.user.dto.CreateUserRequest;
import com.inventory.system.user.dto.UpdateUserRequest;
import com.inventory.system.user.dto.UpdateUserRoleRequest;
import com.inventory.system.user.entity.Role;
import com.inventory.system.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    // নতুন ইউজার তৈরি (পাসওয়ার্ড এনক্রিপ্ট করে সেভ হয়)
    @Transactional
    public User createUser(CreateUserRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            throw new RuntimeException("User already exists with email: " + request.getEmail());
        });

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setRoles(resolveRoles(request.getRoles()));
        return userRepository.save(user);
    }

    // সব ইউজারের তালিকা দেখা
    public List<User> getAllUsers() {
        return userRepository.findAllByDeletedFalse();
    }

    public User getUserById(Long id) {
        return userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    // নাম / ইমেল / অ্যাকটিভ স্ট্যাটাস আপডেট
    @Transactional
    public User updateUser(Long id, UpdateUserRequest request) {
        User user = getUserById(id);

        userRepository.findByEmail(request.getEmail())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new RuntimeException("User already exists with email: " + request.getEmail());
                });

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        return userRepository.save(user);
    }

    // সুপার এডমিন কর্তৃক অন্য ইউজারের রোল বা পারমিশন আপডেট করা
    @Transactional
    public User updateUserRoles(Long userId, UpdateUserRoleRequest request) {
        User user = getUserById(userId);
        user.setRoles(resolveRoles(request.getRoles()));
        return userRepository.save(user);
    }

    // সফট ডিলিট: ইউজার আর লগইন করতে পারবে না, কিন্তু পুরনো সেল রেকর্ডে soldBy থেকে যাবে
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        user.setDeleted(true);
        user.setActive(false);
        userRepository.save(user);
    }

    private Set<Role> resolveRoles(Set<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
            roles.add(role);
        }
        return roles;
    }
}
