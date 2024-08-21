package com.work.erpsystem.api;

import com.work.erpsystem.service.impl.auth.UserAuthenticationImpl;
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
