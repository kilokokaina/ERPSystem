package com.work.erpsystem.api;

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

@Slf4j
@RestController
@RequestMapping("api/auth")
public class AuthAPI {

    private final UserAuthenticationImpl userAuthentication;
    private final HttpServletResponse servletResponse;
    private final HttpServletRequest servletRequest;

    @Autowired
    public AuthAPI(HttpServletResponse servletResponse, HttpServletRequest servletRequest
            , UserAuthenticationImpl userAuthentication) {
        this.userAuthentication = userAuthentication;
        this.servletResponse = servletResponse;
        this.servletRequest = servletRequest;
    }

    @PostMapping("login")
    public ResponseEntity<HttpStatus> login(@RequestParam(value = "username") String username,
                                            @RequestParam(value = "password") String password) {
        boolean status = userAuthentication.startSession(username, password);
        HttpHeaders headers = new HttpHeaders();

        if (status) {
            RequestCache requestCache = new HttpSessionRequestCache();
            SavedRequest savedRequest = requestCache.getRequest(servletRequest, servletResponse);

            headers.add("Location", savedRequest.getRedirectUrl());
        }
        else headers.add("Location", "/login");

        return new ResponseEntity<>(headers, HttpStatus.valueOf(301));
    }

    @PostMapping("register")
    public ResponseEntity<HttpStatus> register(@RequestParam(value = "username") String username,
                                               @RequestParam(value = "password") String password) {
        userAuthentication.register(username, password);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/login");

        return new ResponseEntity<>(headers, HttpStatus.valueOf(301));
    }

}
