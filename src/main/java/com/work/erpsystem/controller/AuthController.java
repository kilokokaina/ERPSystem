package com.work.erpsystem.controller;

import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("register")
public class AuthController {

    private final UserServiceImpl userService;

    @Autowired
    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public String registerGet() {
        return "login";
    }

    @PostMapping
    public String registerPost(@RequestParam(name = "username") String username,
                               @RequestParam(name = "password") String password) {
        UserModel userModel = new UserModel();

        userModel.setUsername(username);
        userModel.setPassword(password);

        userService.save(userModel);

        return "redirect:/login";
    }

}
