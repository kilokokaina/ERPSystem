package com.work.erpsystem.service;

import org.springframework.http.HttpStatus;

public interface UserAuthenticationService {

    boolean startSession(String username, String password);
    boolean register(String username, String password);

}
