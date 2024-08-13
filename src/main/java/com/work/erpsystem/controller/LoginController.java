package com.work.erpsystem.controller;

import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Slf4j
@Controller
public class LoginController {

    private final UserServiceImpl userService;

    @Autowired
    public LoginController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("login")
    public String login() {
        SecurityContext context = SecurityContextHolder.getContext();
        log.info(context.getAuthentication().toString());

        return "login";
    }

    @GetMapping("set_new_password/{uuid}")
    public String setNewUser(@PathVariable(value = "uuid") String uuid, Model model) throws NoDBRecord {
        if (userService.findByUUID(uuid) == null) return "redirect:/error";
        model.addAttribute("uuid", uuid);
        return "change-password";
    }

    @PostMapping("set_new_password/{uuid}")
    public String saveNewPassword(@PathVariable(value = "uuid") String uuid,
                                  @RequestParam(value = "password") String newPassword) throws NoDBRecord {
        UserModel userModel = userService.findByUUID(uuid);
        userModel.setPassword(newPassword);
        userModel.setUserUUID(UUID.randomUUID().toString());
        userService.update(userModel);

        return "redirect:/login";
    }

}
