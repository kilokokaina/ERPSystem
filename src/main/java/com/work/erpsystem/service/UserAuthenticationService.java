package com.work.erpsystem.service;

import org.springframework.http.HttpStatus;

public interface UserAuthenticationService {

    HttpStatus startSession(String username, String password);
    HttpStatus register(String username, String password);

}
