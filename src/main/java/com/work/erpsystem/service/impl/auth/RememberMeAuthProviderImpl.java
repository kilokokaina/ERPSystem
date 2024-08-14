package com.work.erpsystem.service.impl.auth;

import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RememberMeAuthProviderImpl implements AuthenticationProvider {

    private @Value("${secret.key}") String secretKey;

    private final UserServiceImpl userService;
    private final HttpServletRequest request;

    @Autowired
    public RememberMeAuthProviderImpl(UserServiceImpl userService, HttpServletRequest request) {
        this.userService = userService;
        this.request = request;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserModel userModel = (UserModel) authentication.getPrincipal();

        UserDetails userDetails = userService.loadUserByUsername(userModel.getUsername());

        if (userDetails == null) return null;

        log.info(userDetails.getUsername());
        log.info(request.getRemoteAddr());

        return new RememberMeAuthenticationToken(
                secretKey, userModel, userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(RememberMeAuthenticationToken.class);
    }
}
