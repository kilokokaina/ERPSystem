package com.work.erpsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MainController {

    @GetMapping("{org_uuid}")
    public String home(@PathVariable(value = "org_uuid") Long orgId, Model model) {
        model.addAttribute("orgId", orgId);
        return "index";
    }

    @GetMapping
    public String homeWithoutOrg() {
        return "redirect:/choose_org";
    }

}
