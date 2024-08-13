package com.work.erpsystem.service.impl.auth;

import com.work.erpsystem.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final UserServiceImpl userService;
    private final HttpServletRequest request;

    @Autowired
    public AuthenticationProviderImpl(UserServiceImpl userService, HttpServletRequest request) {
        this.userService = userService;
        this.request = request;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        UserDetails userDetails = userService.loadUserByUsername(username);

        if (userDetails == null || !userDetails.getPassword().equals(password)) return null;

        log.info(userDetails.getUsername());
        log.info(request.getRemoteAddr());

        return new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(), userDetails.getPassword(),
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
