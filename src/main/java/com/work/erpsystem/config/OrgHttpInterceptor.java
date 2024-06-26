package com.work.erpsystem.config;

import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class OrgHttpInterceptor implements HandlerInterceptor {

    private final UserServiceImpl userService;

    @Autowired
    public OrgHttpInterceptor(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        UserModel userModel = userService.findByUsername(request.getRemoteUser());

        if (userModel != null) {
            if (userModel.getOrgRole().isEmpty()) {
                String redirectURL = response.encodeRedirectURL(request.getContextPath() + "/create_org");
                response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
                response.setHeader("Location", redirectURL);

                log.info(request.getRequestURI());

                return false;
            }
        }

        return true;
    }
}
