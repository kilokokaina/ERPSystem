package com.work.erpsystem.controller;

import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.CategoryModel;
import com.work.erpsystem.model.OrganizationModel;
import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.model.WarehouseModel;
import com.work.erpsystem.service.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class OrgController {

    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;
    private final CategoryServiceImpl categoryService;
    private final WarehouseServiceImpl warehouseService;
    private final OrgServiceImpl orgService;

    @Autowired
    public OrgController(UserServiceImpl userService, CategoryServiceImpl categoryService, OrgServiceImpl orgService,
                         WarehouseServiceImpl warehouseService, ItemServiceImpl itemService) {
        this.warehouseService = warehouseService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.itemService = itemService;
        this.orgService = orgService;
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


    @GetMapping("{org_uuid}/organization")
    public String orgHome(@PathVariable(value = "org_uuid") Long orgId, Model model, Authentication authentication) {
        try {
            OrganizationModel organizationModel = orgService.findById(orgId);

            List<UserModel> employees = userService.findByEmployeeOrg(organizationModel);
            List<WarehouseModel> warehouses = warehouseService.findByOrganization(organizationModel);
            Map<CategoryModel, Integer> categories = new HashMap<>();

            categoryService.findByOrg(organizationModel).forEach(category ->
                    categories.put(category, itemService.findByCategoryId(category.getCategoryId()).size())
            );

            model.addAttribute("organization", organizationModel);
            model.addAttribute("employees", employees);
            model.addAttribute("warehouses", warehouses);
            model.addAttribute("categories", categories);
            model.addAttribute("orgId", orgId);

            return "organization";
        } catch (NoDBRecord exception) {
            return "redirect:/error";
        }
    }

}
