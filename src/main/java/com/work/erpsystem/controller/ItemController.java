package com.work.erpsystem.controller;

import com.work.erpsystem.model.CategoryModel;
import com.work.erpsystem.service.impl.CategoryServiceImpl;
import com.work.erpsystem.service.impl.ItemServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("item")
public class ItemController {

    private final ItemServiceImpl itemService;
    private final CategoryServiceImpl categoryService;

    @Autowired
    public ItemController(ItemServiceImpl itemService, CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
        this.itemService = itemService;
    }

    @GetMapping
    public String home(@RequestParam(value = "category") CategoryModel category, Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("items", itemService.findByCategoryId(category.getCategoryId()));

        return "item";
    }

}
