package com.work.erpsystem.controller;

import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.model.WarehouseModel;
import com.work.erpsystem.service.impl.UserServiceImpl;
import com.work.erpsystem.service.impl.WarehouseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("warehouse")
public class StorageController {

    private final WarehouseServiceImpl warehouseService;
    private final UserServiceImpl userService;

    @Autowired
    public StorageController(WarehouseServiceImpl warehouseService, UserServiceImpl userService) {
        this.warehouseService = warehouseService;
        this.userService = userService;
    }

    @GetMapping("storage")
    public String storageList(Authentication authentication, Model model) {
        UserModel userModel = userService.findByUsername(authentication.getName());

        List<WarehouseModel> warehouseList = warehouseService.findByOrganization(userModel.getOrgEmployee());
        model.addAttribute("warehouses", warehouseList);

        return "storage";
    }

    @GetMapping("sales")
    public String salesList() {
        return "";
    }

}
