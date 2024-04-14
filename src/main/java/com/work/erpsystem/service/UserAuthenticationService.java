package com.work.erpsystem.service;

import com.work.erpsystem.exception.BadCredentials;
import org.springframework.http.HttpStatus;

public interface UserAuthenticationService {

    void startSession(String username, String password) throws BadCredentials;
    boolean register(String username, String password);

}
