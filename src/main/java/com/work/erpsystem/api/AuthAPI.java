package com.work.erpsystem.api;

import com.work.erpsystem.service.impl.UserAuthenticationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthAPI {

    private final UserAuthenticationImpl userAuthentication;

    @Autowired
    public AuthAPI(UserAuthenticationImpl userAuthentication) {
        this.userAuthentication = userAuthentication;
    }

    @PostMapping("login")
    public ResponseEntity<HttpStatus> login(@RequestParam(value = "username") String username,
                                            @RequestParam(value = "password") String password) {
        HttpStatus status = userAuthentication.startSession(username, password);
        HttpHeaders headers = new HttpHeaders();

        if (status.value() == 200) headers.add("Location", "/");
        else headers.add("Location", "/login");

        return new ResponseEntity<>(headers, status);
    }

    @PostMapping("register")
    public ResponseEntity<HttpStatus> register(@RequestParam(value = "username") String username,
                                               @RequestParam(value = "password") String password) {
        HttpStatus status = userAuthentication.register(username, password);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/login");

        return new ResponseEntity<>(headers, status);
    }

}
