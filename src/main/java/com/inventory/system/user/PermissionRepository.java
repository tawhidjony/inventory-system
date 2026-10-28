package com.inventory.system.user;

import com.inventory.system.user.entity.Permission;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

}