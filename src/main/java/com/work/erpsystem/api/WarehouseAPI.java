package com.work.erpsystem.api;

import com.work.erpsystem.dto.WarehouseDTO;
import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.OrganizationModel;
import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.model.WarehouseModel;
import com.work.erpsystem.service.impl.UserServiceImpl;
import com.work.erpsystem.service.impl.WarehouseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/warehouse")
public class WarehouseAPI {

    private final WarehouseServiceImpl warehouseService;
    private final UserServiceImpl userService;

    @Autowired
    public WarehouseAPI(WarehouseServiceImpl warehouseService, UserServiceImpl userService) {
        this.warehouseService = warehouseService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<WarehouseModel>> findAll(Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());
        OrganizationModel organization = userModel.getOrgEmployee();

        return ResponseEntity.ok(warehouseService.findByOrganization(organization));
    }

    @GetMapping("{id}")
    public ResponseEntity<WarehouseModel> findById(@PathVariable(value = "id") Long warehouseId,
                                                   Authentication authentication) {
        try {
            UserModel userModel = userService.findByUsername(authentication.getName());
            WarehouseModel warehouse = warehouseService.findById(warehouseId);

            if (!warehouse.getOrganization().equals(userModel.getOrgEmployee())) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            return ResponseEntity.ok(warehouse);

        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping
    public ResponseEntity<WarehouseModel> addWarehouse(@RequestBody WarehouseDTO warehouseDto,
                                                       Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());
        WarehouseModel warehouseModel = new WarehouseModel();

        warehouseModel.setOrganization(userModel.getOrgEmployee());
        warehouseModel.setWarehouseName(warehouseDto.getWarehouseName());
        warehouseModel.setWarehouseAddress(warehouseDto.getWarehouseAddress());

        try {
            return ResponseEntity.ok(warehouseService.save(warehouseModel));
        } catch (DuplicateDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<WarehouseModel> updateWarehouse(@PathVariable(value = "id") Long warehouseId,
                                                          @RequestBody WarehouseModel warehouseNew,
                                                          Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());

        try {
            WarehouseModel warehouseModel = warehouseService.findById(warehouseId);

            if (!warehouseModel.getOrganization().equals(userModel.getOrgEmployee())) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            warehouseModel.setWarehouseName(warehouseNew.getWarehouseName());
            warehouseModel.setWarehouseAddress(warehouseNew.getWarehouseAddress());

            return ResponseEntity.ok(warehouseService.update(warehouseModel));
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> deleteWarehouse(@PathVariable(value = "id") Long warehouseId,
                                                          Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());

        try {
            WarehouseModel warehouseModel = warehouseService.findById(warehouseId);

            if (!warehouseModel.getOrganization().equals(userModel.getOrgEmployee())) {
                return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
            }

            warehouseService.deleteById(warehouseId);

            return ResponseEntity.ok(HttpStatus.OK);

        } catch (NoDBRecord exception) {
            return ResponseEntity.ok(HttpStatus.NO_CONTENT);
        }
    }

}
