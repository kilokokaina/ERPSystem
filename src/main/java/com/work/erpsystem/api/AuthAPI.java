package com.work.erpsystem.api;

import com.work.erpsystem.exception.BadCredentials;
import com.work.erpsystem.service.impl.UserAuthenticationImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("api/auth")
public class AuthAPI {

    private final UserAuthenticationImpl userAuthentication;
    private final HttpServletResponse servletResponse;
    private final HttpServletRequest servletRequest;

    @Autowired
    public AuthAPI(HttpServletResponse servletResponse, HttpServletRequest servletRequest,
                   UserAuthenticationImpl userAuthentication) {
        this.userAuthentication = userAuthentication;
        this.servletResponse = servletResponse;
        this.servletRequest = servletRequest;
    }

    @PostMapping("login")
    public @ResponseBody ResponseEntity<HttpStatus> login(@RequestParam(value = "username") String username,
                                                          @RequestParam(value = "password") String password) {
        HttpHeaders headers = new HttpHeaders();
        try {
            userAuthentication.startSession(username, password);

            RequestCache requestCache = new HttpSessionRequestCache();
            SavedRequest savedRequest = requestCache.getRequest(servletRequest, servletResponse);

            if (Objects.isNull(savedRequest) || savedRequest.getRedirectUrl().contains("error")) {
                headers.add("Location", "/");
            } else {
                headers.add("Location", savedRequest.getRedirectUrl());
                log.info(savedRequest.getRedirectUrl());
            }
        } catch (BadCredentials exception) {
            headers.add("Location", "/login");
            log.info(exception.getMessage());
        }

        return new ResponseEntity<>(headers, HttpStatus.valueOf(301));
    }

    @PostMapping("register")
    public @ResponseBody ResponseEntity<HttpStatus> register(@RequestParam(value = "username") String username,
                                                             @RequestParam(value = "password") String password,
                                                             @RequestParam(value = "firstname") String firstName,
                                                             @RequestParam(value = "secondname") String secondName) {
        userAuthentication.register(username, password, firstName, secondName);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/login");

        return new ResponseEntity<>(headers, HttpStatus.valueOf(301));
    }

}
