package com.work.erpsystem.service;

import com.work.erpsystem.exception.BadCredentials;
import org.springframework.http.HttpStatus;

public interface UserAuthenticationService {

    boolean register(String username, String password, String firstName, String secondName);

}
