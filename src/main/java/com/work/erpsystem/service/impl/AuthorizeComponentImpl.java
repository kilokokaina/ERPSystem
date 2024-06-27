package com.work.erpsystem.service.impl;

import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.OrganizationModel;
import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.service.AuthorizeComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component("preAuth")
public class AuthorizeComponentImpl implements AuthorizeComponent {

    private final UserServiceImpl userService;
    private final OrgServiceImpl orgService;

    @Autowired
    public AuthorizeComponentImpl(UserServiceImpl userService, OrgServiceImpl orgService) {
        log.info("Bean " + this.getClass().getName() + " created");

        this.userService = userService;
        this.orgService = orgService;
    }

    @Override
    public boolean getAuthorities(@NonNull Authentication authentication, @NonNull Long orgId, String role) {
        try {
            UserModel userModel = userService.findByUsername(authentication.getName());
            OrganizationModel organizationModel = orgService.findById(orgId);

            String orgRole = userModel.getOrgRole().get(organizationModel);

            return role.contains(orgRole);
        } catch (NoDBRecord exception) {
            log.error(exception.getMessage());
            return false;
        }
    }

}
