package com.work.erpsystem.controller;

import com.work.erpsystem.model.CategoryModel;
import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.service.impl.CategoryServiceImpl;
import com.work.erpsystem.service.impl.ItemServiceImpl;
import com.work.erpsystem.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("item")
@PreAuthorize("hasAuthority('USER')")
public class ItemController {

    private final ItemServiceImpl itemService;
    private final CategoryServiceImpl categoryService;
    private final UserServiceImpl userService;

    @Autowired
    public ItemController(ItemServiceImpl itemService, CategoryServiceImpl categoryService,
                          UserServiceImpl userService) {
        this.categoryService = categoryService;
        this.itemService = itemService;
        this.userService = userService;
    }

    @GetMapping
    public String home(@RequestParam(value = "category", required = false) CategoryModel category,
                       Authentication authentication, Model model) {
        UserModel userModel = userService.findByUsername(authentication.getName());

        model.addAttribute("categories", categoryService.findByOrg(userModel.getOrgEmployee()));

        if (Objects.nonNull(category)) {
            model.addAttribute("categoryName", category.getCategoryName());
            model.addAttribute("items", itemService.findByCategoryId(category.getCategoryId()));
        } else model.addAttribute("items", null);

        log.info(authentication.getPrincipal().toString());

        return "item";
    }

}
