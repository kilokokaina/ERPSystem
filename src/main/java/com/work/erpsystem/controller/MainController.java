package com.work.erpsystem.controller;

import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Controller
public class MainController {

    private final UserServiceImpl userService;

    @Autowired
    public MainController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("{org_uuid}")
    public String home(@PathVariable(value = "org_uuid") Long orgId, Model model) {
        model.addAttribute("orgId", orgId);
        return "index";
    }

    @GetMapping
    public String homeWithoutOrg() {
        return "redirect:/choose_org";
    }

    @GetMapping("choose_org")
    public String chooseOrg(Model model, Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());
        model.addAttribute("orgList", userModel.getOrgRole().keySet());

        return "choose-org";
    }

    @GetMapping("create_org")
    public String createOrg() {
        return "create-org";
    }

    @GetMapping("set_new_user/{uuid}")
    public String setNewUser(@PathVariable(value = "uuid") String uuid, Model model) throws NoDBRecord{
        if (userService.findByUUID(uuid) == null) return "redirect:/error";
        model.addAttribute("uuid", uuid);
        return "set-user";
    }

    @GetMapping("login")
    public String login() {
        return "login";
    }

    @PostMapping("set_new_user/{uuid}")
    public String saveNewPassword(@PathVariable(value = "uuid") String uuid,
                                  @RequestParam(value = "password") String newPassword) throws NoDBRecord {
        UserModel userModel = userService.findByUUID(uuid);
        userModel.setPassword(newPassword);
        userModel.setUserUUID(UUID.randomUUID().toString());
        userService.update(userModel);

        return "redirect:/login";
    }

}
