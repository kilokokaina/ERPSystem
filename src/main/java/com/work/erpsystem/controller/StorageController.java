package com.work.erpsystem.controller;

import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.model.WarehouseModel;
import com.work.erpsystem.service.impl.CategoryServiceImpl;
import com.work.erpsystem.service.impl.UserServiceImpl;
import com.work.erpsystem.service.impl.WarehouseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("warehouse")
public class StorageController {

    private final WarehouseServiceImpl warehouseService;
    private final CategoryServiceImpl categoryService;
    private final UserServiceImpl userService;

    @Autowired
    public StorageController(WarehouseServiceImpl warehouseService, CategoryServiceImpl categoryService,
                             UserServiceImpl userService) {
        this.warehouseService = warehouseService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping("storage")
    public String storageList(@RequestParam(value = "id", required = false) WarehouseModel warehouseModel,
                              Authentication authentication, Model model) {
        UserModel userModel = userService.findByUsername(authentication.getName());

        model.addAttribute("categories", categoryService.findByOrg(userModel.getOrgEmployee()));

        if (Objects.nonNull(warehouseModel)) {
            model.addAttribute("itemQuantity", warehouseModel.getItemQuantity());
            model.addAttribute("warehouseName", warehouseModel.getWarehouseName());
            model.addAttribute("warehouseId", warehouseModel.getWarehouseId());
        }

        List<WarehouseModel> warehouseList = warehouseService.findByOrganization(userModel.getOrgEmployee());
        model.addAttribute("warehouses", warehouseList);

        return "storage";
    }

    @GetMapping("sales")
    public String salesList() {
        return "sales";
    }

}
