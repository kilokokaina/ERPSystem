package com.work.erpsystem.controller;

import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.OrganizationModel;
import com.work.erpsystem.model.TransitModel;
import com.work.erpsystem.model.WarehouseModel;
import com.work.erpsystem.repository.SaleRepository;
import com.work.erpsystem.repository.TransitRepository;
import com.work.erpsystem.service.impl.CategoryServiceImpl;
import com.work.erpsystem.service.impl.OrgServiceImpl;
import com.work.erpsystem.service.impl.WarehouseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("{org_uuid}/warehouse")
public class StorageController {

    private final WarehouseServiceImpl warehouseService;
    private final TransitRepository transitRepository;
    private final CategoryServiceImpl categoryService;
    private final SaleRepository saleRepository;
    private final OrgServiceImpl orgService;

    @Autowired
    public StorageController(WarehouseServiceImpl warehouseService, TransitRepository transitRepository,
                             CategoryServiceImpl categoryService, SaleRepository saleRepository,
                             OrgServiceImpl orgService) {
        this.warehouseService = warehouseService;
        this.transitRepository = transitRepository;
        this.categoryService = categoryService;
        this.saleRepository = saleRepository;
        this.orgService = orgService;
    }

    @GetMapping("storage")
    public String storageList(@RequestParam(value = "id", required = false) WarehouseModel warehouseModel,
                              Authentication authentication, Model model, @PathVariable(value = "org_uuid") Long orgId) {
        try {
            OrganizationModel organizationModel = orgService.findById(orgId);
            model.addAttribute("categories", categoryService.findByOrg(organizationModel));

            if (Objects.nonNull(warehouseModel)) {
                model.addAttribute("itemQuantity", warehouseModel.getItemQuantity());
                model.addAttribute("itemPrice", warehouseModel.getItemPrice());
                model.addAttribute("warehouseName", warehouseModel.getWarehouseName());
                model.addAttribute("warehouseId", warehouseModel.getWarehouseId());
            }

            List<WarehouseModel> warehouseList = warehouseService.findByOrganization(organizationModel);
            model.addAttribute("warehouses", warehouseList);
            model.addAttribute("orgId", orgId);

            return "storage";
        } catch (NoDBRecord exception) {
            return "redirect:/error";
        }
    }

    @GetMapping("sales")
    public String salesList(@RequestParam(value = "id", required = false) WarehouseModel warehouseModel,
                            Authentication authentication, Model model, @PathVariable(value = "org_uuid") Long orgId) {
        try {
            OrganizationModel organizationModel = orgService.findById(orgId);

            if (Objects.nonNull(warehouseModel)) {
                model.addAttribute("itemSales", saleRepository.findByWarehouse(warehouseModel));
                model.addAttribute("itemQuantity", warehouseModel.getItemQuantity());
                model.addAttribute("warehouseName", warehouseModel.getWarehouseName());
                model.addAttribute("warehouseId", warehouseModel.getWarehouseId());
            }

            List<WarehouseModel> warehouseList = warehouseService.findByOrganization(organizationModel);
            model.addAttribute("warehouses", warehouseList);
            model.addAttribute("orgId", orgId);

            return "sales";
        } catch (NoDBRecord exception) {
            return "redirect:/error";
        }
    }

    @GetMapping("transit")
    public String transit(@RequestParam(value = "id", required = false) WarehouseModel warehouseModel,
                          Authentication authentication, Model model, @PathVariable(value = "org_uuid") Long orgId) {
        try {
            OrganizationModel organizationModel = orgService.findById(orgId);

            if (Objects.nonNull(warehouseModel)) {
                model.addAttribute("itemQuantity", warehouseModel.getItemQuantity());
                model.addAttribute("warehouseName", warehouseModel.getWarehouseName());
                model.addAttribute("warehouseId", warehouseModel.getWarehouseId());
                model.addAttribute("itemPrice", warehouseModel.getItemPrice());
            }

            List<WarehouseModel> warehouseList = warehouseService.findByOrganization(organizationModel);
            List<TransitModel> transitList = transitRepository.findByOrganization(organizationModel);

            model.addAttribute("warehouses", warehouseList);
            model.addAttribute("transits", transitList);
            model.addAttribute("orgId", orgId);

            return "delivery";
        } catch (NoDBRecord exception) {
            return "redirect:/error";
        }

    }

}
