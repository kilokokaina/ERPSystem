package com.work.erpsystem.controller;

import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("{org_uuid}/settings")
    public String settings(@PathVariable(value = "org_uuid") Long orgId, Authentication authentication, Model model) {
        UserModel userModel = userService.findByUsername(authentication.getName());

        model.addAttribute("user", userModel);
        model.addAttribute("orgId", orgId);

        return "settings";
    }

    @GetMapping
    public String homeWithoutOrg() {
        return "redirect:/choose_org";
    }

}
