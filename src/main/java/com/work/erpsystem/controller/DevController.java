package com.work.erpsystem.controller;

import com.work.erpsystem.dto.WarehouseDTO;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.ItemModel;
import com.work.erpsystem.model.OrganizationModel;
import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.model.WarehouseModel;
import com.work.erpsystem.repository.OrganizationRepository;
import com.work.erpsystem.repository.WarehouseRepository;
import com.work.erpsystem.service.impl.ItemServiceImpl;
import com.work.erpsystem.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class DevController {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ItemServiceImpl itemService;

    @GetMapping("route")
    public String dev() {
        return "index";
    }

    @PostMapping("add_org")
    public ResponseEntity<OrganizationModel> addOrganization(@RequestBody OrganizationModel organizationModel,
                                                             Authentication authentication) {
        OrganizationModel organization = organizationRepository.save(organizationModel);

        UserModel userModel = userService.findByUsername(authentication.getName());
        userModel.setOrgOwner(organization);
        userService.save(userModel);

        return ResponseEntity.ok(organization);
    }

    @PostMapping("add_warehouse")
    public ResponseEntity<WarehouseModel> addWarehouse(@RequestBody WarehouseDTO warehouseDTO,
                                                       Authentication authentication) {
        WarehouseModel warehouseModel = new WarehouseModel();
        OrganizationModel organizationModel = userService.findByUsername(authentication.getName()).getOrgOwner();

        warehouseModel.setWarehouseName(warehouseDTO.getWarehouseName());
        warehouseModel.setOrganization(organizationModel);

        return ResponseEntity.ok(warehouseRepository.save(warehouseModel));
    }

    @PostMapping("update_warehouse/{id}")
    public ResponseEntity<WarehouseModel> updateWarehouse(@PathVariable(value = "id") Long warehouseId) throws NoDBRecord {
        WarehouseModel warehouseModel = warehouseRepository.findById(warehouseId).orElse(null);

//        ItemModel item1 = itemService.findById(1L);
//        ItemModel item2 = itemService.findById(2L);
//        ItemModel item3 = itemService.findById(3L);

        assert warehouseModel != null;
        Map<ItemModel, Integer> itemQuantity = warehouseModel.getItemQuantity();
        for (Map.Entry<ItemModel, Integer> entry : itemQuantity.entrySet()) {
            if (entry.getKey().getItemId() == 2) {
                int quantity = entry.getValue();
                entry.setValue(quantity + 3);
            }
        }

        warehouseModel.setItemQuantity(itemQuantity);

        return ResponseEntity.ok(warehouseRepository.save(warehouseModel));
    }

    @GetMapping("get_ip")
    public @ResponseBody String getIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

}
