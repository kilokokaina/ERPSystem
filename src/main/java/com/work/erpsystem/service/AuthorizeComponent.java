package com.work.erpsystem.service;

import org.springframework.security.core.Authentication;

public interface AuthorizeComponent {

    boolean getAuthorities(Authentication authentication, Long orgId, String role);

}
