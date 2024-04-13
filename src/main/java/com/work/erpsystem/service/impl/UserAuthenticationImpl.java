package com.work.erpsystem.service.impl;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.service.UserAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class UserAuthenticationImpl implements UserAuthenticationService {

    private final SecurityContextRepository contextRepository;
    private final AuthenticationManagerImpl authenticationManager;
    private final HttpServletResponse servletResponse;
    private final HttpServletRequest servletRequest;
    private final UserServiceImpl userService;

    @Autowired
    public UserAuthenticationImpl(AuthenticationManagerImpl authenticationManager, HttpServletResponse servletResponse,
                                  HttpServletRequest servletRequest, UserServiceImpl userService) {
        this.contextRepository = new HttpSessionSecurityContextRepository();
        this.authenticationManager = authenticationManager;
        this.servletResponse = servletResponse;
        this.servletRequest = servletRequest;
        this.userService = userService;
    }

    @Override
    public boolean startSession(String username, String password) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        authentication = authenticationManager.authenticate(authentication);

        if (Objects.nonNull(authentication)) {
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);

            contextRepository.saveContext(securityContext, servletRequest, servletResponse);

            log.info(servletRequest.getRemoteAddr());

            return true;
        }

        return false;
    }

    @Override
    public boolean register(String username, String password) {
        try {
            UserModel userModel = new UserModel();

            userModel.setUsername(username);
            userModel.setPassword(password);

            userService.save(userModel);
        } catch (DuplicateDBRecord exception) {
            return false;
        }

        return true;
    }
}
