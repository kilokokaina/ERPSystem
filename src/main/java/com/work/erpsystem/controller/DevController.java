package com.work.erpsystem.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class DevController {

    @GetMapping("route")
    public String dev() {
        return "index";
    }

}
