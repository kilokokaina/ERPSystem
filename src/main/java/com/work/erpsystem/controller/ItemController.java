package com.work.erpsystem.controller;

import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.*;
import com.work.erpsystem.service.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Slf4j
@Controller
@RequestMapping("{org_uuid}/item")
@PreAuthorize("@preAuth.getAuthorities(#auth, #org, 'USER, OWNER, ADMIN')")
public class ItemController {

    private final ItemServiceImpl itemService;
    private final CategoryServiceImpl categoryService;
    private final WarehouseServiceImpl warehouseService;
    private final OrgServiceImpl orgService;

    @Autowired
    public ItemController(ItemServiceImpl itemService, CategoryServiceImpl categoryService,
                          OrgServiceImpl orgService, WarehouseServiceImpl warehouseService) {
        this.categoryService = categoryService;
        this.warehouseService = warehouseService;
        this.itemService = itemService;
        this.orgService = orgService;
    }

    @GetMapping
    public String home(@RequestParam(value = "category", required = false) CategoryModel category,
                       @P("auth") Authentication authentication, @P("org") @PathVariable(value = "org_uuid") Long orgId,
                       Model model) throws NoDBRecord {
        try {
            model.addAttribute("categories", categoryService.findByOrg(orgService.findById(orgId)));

            List<ItemModel> itemList;
            if (Objects.nonNull(category)) {
                itemList = itemService.findByCategoryId(category.getCategoryId());
                itemList.sort(Comparator.comparingLong(ItemModel::getItemId));

                model.addAttribute("categoryName", category.getCategoryName());
                model.addAttribute("items", itemList);
            } else {
                itemList = itemService.findByOrganizationModel(orgService.findById(orgId));
                itemList.sort(Comparator.comparingLong(ItemModel::getItemId));
                model.addAttribute("items", itemList);
            }

            model.addAttribute("orgId", orgId);

            return "item";
        } catch (NoDBRecord exception) {
            return "redirect:/error";
        }
    }

    @GetMapping("{id}")
    public String itemPage(@PathVariable(value = "id") Long itemId, @P("auth") Authentication authentication,
                           @P("org") @PathVariable(value = "org_uuid") Long orgId, Model model) {
        try {
            ItemModel itemModel = itemService.findById(itemId);

            int itemQuantity = 0;
            Map<WarehouseModel, Double> warehouseItemPrice = new HashMap<>();
            List<WarehouseModel> warehouseList = warehouseService.findByOrganization(orgService.findById(orgId));

            for (WarehouseModel warehouse : warehouseList) {
                if (warehouse.getItemPrice().containsKey(itemModel)) {
                    warehouseItemPrice.put(warehouse, warehouse.getItemPrice().get(itemModel));
                    log.info(warehouse.getWarehouseName() + ": " + warehouse.getItemQuantity().get(itemModel));
                    itemQuantity += warehouse.getItemQuantity().get(itemModel);
                }
            }

            model.addAttribute("itemPrice", warehouseItemPrice);
            model.addAttribute("itemQuantity", itemQuantity);
            model.addAttribute("barcode", itemModel.getBarcode());
            model.addAttribute("item", itemModel);
            model.addAttribute("orgId", orgId);

            return "item-page";
        } catch (NoDBRecord exception) {
            log.error(exception.getMessage());
            return "redirect:/error";
        }
    }

}
