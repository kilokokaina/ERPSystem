package com.work.erpsystem.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    SUPER_USER, OWNER, ADMIN, USER;

    @Override
    public String getAuthority() {
        return name();
    }

}
