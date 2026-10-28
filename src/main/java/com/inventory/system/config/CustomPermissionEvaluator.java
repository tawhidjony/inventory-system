package com.inventory.system.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component("customPermissionEvaluator")
public class CustomPermissionEvaluator {
    public boolean hasPermission(Authentication authentication, String requiredPermission){
        if(authentication == null){
            return false;
        }

        for (GrantedAuthority authority: authentication.getAuthorities()){
            if (authority.getAuthority().equals("ROLE_SUPER_ADMIN")){
                return true;
            }
            if (authority.getAuthority().equals(requiredPermission)) {
                return true;
            }
        }
        return false;
    }
}
