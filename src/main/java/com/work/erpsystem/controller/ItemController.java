package com.work.erpsystem.controller;

import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.CategoryModel;
import com.work.erpsystem.model.ItemModel;
import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.model.WarehouseModel;
import com.work.erpsystem.service.impl.CategoryServiceImpl;
import com.work.erpsystem.service.impl.ItemServiceImpl;
import com.work.erpsystem.service.impl.UserServiceImpl;
import com.work.erpsystem.service.impl.WarehouseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("item")
@PreAuthorize("hasAuthority('USER')")
public class ItemController {

    private final ItemServiceImpl itemService;
    private final CategoryServiceImpl categoryService;
    private final WarehouseServiceImpl warehouseService;
    private final UserServiceImpl userService;

    @Autowired
    public ItemController(ItemServiceImpl itemService, CategoryServiceImpl categoryService,
                          WarehouseServiceImpl warehouseService, UserServiceImpl userService) {
        this.categoryService = categoryService;
        this.warehouseService = warehouseService;
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

        return "item";
    }

    @GetMapping("add")
    public String add() {
        return "add-item";
    }

    @GetMapping("{id}")
    public String itemPage(@PathVariable(value = "id") Long itemId, Model model,
                           Authentication authentication) {
        try {
            ItemModel itemModel = itemService.findById(itemId);
            UserModel userModel = userService.findByUsername(authentication.getName());

            int itemQuantity = 0;
            Map<WarehouseModel, Double> warehouseItemPrice = new HashMap<>();
            List<WarehouseModel> warehouseList = warehouseService.findByOrganization(userModel.getOrgEmployee());

            for (WarehouseModel warehouse : warehouseList) {
                if (warehouse.getItemPrice().containsKey(itemModel)) {
                    warehouseItemPrice.put(warehouse, warehouse.getItemPrice().get(itemModel));
                    itemQuantity += warehouse.getItemQuantity().get(itemModel);
                }
            }

            model.addAttribute("itemPrice", warehouseItemPrice);
            model.addAttribute("itemQuantity", itemQuantity);
            model.addAttribute("item", itemModel);

            return "item-page";
        } catch (NoDBRecord exception) {
            log.error(exception.getMessage());
            return "error";
        }
    }

}
