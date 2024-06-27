package com.work.erpsystem.api;

import com.work.erpsystem.dto.ItemQuantityDTO;
import com.work.erpsystem.dto.WarehouseDTO;
import com.work.erpsystem.exception.DBException;
import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.*;
import com.work.erpsystem.repository.SaleRepository;
import com.work.erpsystem.service.impl.ItemServiceImpl;
import com.work.erpsystem.service.impl.OrgServiceImpl;
import com.work.erpsystem.service.impl.UserServiceImpl;
import com.work.erpsystem.service.impl.WarehouseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("{org_uuid}/api/warehouse")
public class WarehouseAPI {

    private final WarehouseServiceImpl warehouseService;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final OrgServiceImpl orgService;

    @Autowired
    public SaleRepository saleRepository;

    @Autowired
    public WarehouseAPI(WarehouseServiceImpl warehouseService, ItemServiceImpl itemService,
                        UserServiceImpl userService, OrgServiceImpl orgService) {
        this.warehouseService = warehouseService;
        this.itemService = itemService;
        this.userService = userService;
        this.orgService = orgService;
    }

    @GetMapping
    public ResponseEntity<List<WarehouseModel>> findAll(@PathVariable(value = "org_uuid") Long orgId,
                                                        Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());
        try {
            if (!userModel.getOrgRole().containsKey(orgService.findById(orgId))) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            return ResponseEntity.ok(warehouseService.findByOrganization(orgService.findById(orgId)));
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<WarehouseModel> findById(@PathVariable(value = "org_uuid") Long orgId,
                                                   @PathVariable(value = "id") Long warehouseId,
                                                   Authentication authentication) {
        try {
            UserModel userModel = userService.findByUsername(authentication.getName());
            WarehouseModel warehouse = warehouseService.findById(warehouseId);

            if (!userModel.getOrgRole().containsKey(orgService.findById(orgId))) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            return ResponseEntity.ok(warehouse);

        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping
    public ResponseEntity<WarehouseModel> addWarehouse(@PathVariable(value = "org_uuid") Long orgId,
                                                       @RequestBody WarehouseDTO warehouseDto) {
        WarehouseModel warehouseModel = new WarehouseModel();

        try {
            warehouseModel.setOrganization(orgService.findById(orgId));
            warehouseModel.setWarehouseName(warehouseDto.getWarehouseName());
            warehouseModel.setWarehouseAddress(warehouseDto.getWarehouseAddress());

            return ResponseEntity.ok(warehouseService.save(warehouseModel));
        } catch (DBException exception) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<WarehouseModel> updateWarehouse(@PathVariable(value = "org_uuid") Long orgId,
                                                          @PathVariable(value = "id") Long warehouseId,
                                                          @RequestBody WarehouseModel warehouseNew,
                                                          Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());

        try {
            WarehouseModel warehouseModel = warehouseService.findById(warehouseId);

            if (!userModel.getOrgRole().containsKey(orgService.findById(orgId))) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            warehouseModel.setWarehouseName(warehouseNew.getWarehouseName());
            warehouseModel.setWarehouseAddress(warehouseNew.getWarehouseAddress());

            return ResponseEntity.ok(warehouseService.update(warehouseModel));
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("add_items/{id}")
    public ResponseEntity<WarehouseModel> addItemsToWarehouse(@PathVariable(value = "org_uuid") Long orgId,
                                                              @PathVariable(value = "id") WarehouseModel warehouseModel,
                                                              @RequestBody ItemQuantityDTO itemQuantityDTO,
                                                              Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());
        try {
            if (!userModel.getOrgRole().containsKey(orgService.findById(orgId))) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            ItemModel itemModel = itemService.findByName(itemQuantityDTO.getItemName());

            Map<ItemModel, Integer> itemQuantity = warehouseModel.getItemQuantity();
            Map<ItemModel, Double> itemPrice = warehouseModel.getItemPrice();

            double itemPriceValue = itemQuantityDTO.getItemPrice();
            itemPrice.put(itemModel, itemPriceValue);

            try {
                int currentItemQuantity = itemQuantity.get(itemModel);
                itemQuantity.put(itemModel, itemQuantityDTO.getQuantity() + currentItemQuantity);
            } catch (NullPointerException exception) {
                itemQuantity.put(itemModel, itemQuantityDTO.getQuantity());
            }

            warehouseModel.setItemQuantity(itemQuantity);
            warehouseModel.setItemPrice(itemPrice);

            return ResponseEntity.ok(warehouseService.update(warehouseModel));

        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("add_sales/{id}")
    public ResponseEntity<WarehouseModel> addSalesToWarehouse(@PathVariable(value = "org_uuid") Long orgId,
                                                              @PathVariable(value = "id") WarehouseModel warehouseModel,
                                                              @RequestBody ItemQuantityDTO itemQuantityDTO,
                                                              Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());
        try {
            if (!userModel.getOrgRole().containsKey(orgService.findById(orgId))) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            SaleModel saleModel = new SaleModel();
            ItemModel itemModel = itemService.findByName(itemQuantityDTO.getItemName());

            saleModel.setWarehouse(warehouseModel);
            saleModel.setItem(itemModel);
            saleModel.setItemSalePrice(warehouseModel.getItemPrice().get(itemModel));
            saleModel.setItemSaleQuantity(itemQuantityDTO.getQuantity());

            if (itemQuantityDTO.getQuantity() > warehouseModel.getItemQuantity().get(itemModel)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                Map<ItemModel, Integer> itemQuantity = warehouseModel.getItemQuantity();

                int currentQuantity = itemQuantity.get(itemModel);
                itemQuantity.put(itemModel, currentQuantity - itemQuantityDTO.getQuantity());
                warehouseModel.setItemQuantity(itemQuantity);

                warehouseService.update(warehouseModel);
            }

            saleRepository.save(saleModel);
        } catch (NoDBRecord exception) {
            log.error(exception.getMessage());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return null;
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> deleteWarehouse(@PathVariable(value = "org_uuid") Long orgId,
                                                      @PathVariable(value = "id") Long warehouseId,
                                                      Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());
        try {
            if (!userModel.getOrgRole().containsKey(orgService.findById(orgId))) {
                return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
            }

            warehouseService.deleteById(warehouseId);

            return ResponseEntity.ok(HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return ResponseEntity.ok(HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping("delete_item/{id}")
    public ResponseEntity<HttpStatus> deleteItemFromWarehouse(@PathVariable(value = "org_uuid") Long orgId,
                                                              @PathVariable(value = "id") WarehouseModel warehouseModel,
                                                              @RequestParam(value = "item_id") Long itemId,
                                                              Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());
        try {
            ItemModel itemModel = itemService.findById(itemId);

            if (!userModel.getOrgRole().containsKey(orgService.findById(orgId))) {
                return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
            }

            Map<ItemModel, Integer> itemQuantity = warehouseModel.getItemQuantity();
            itemQuantity.remove(itemModel);
            warehouseModel.setItemQuantity(itemQuantity);

            warehouseService.update(warehouseModel);

            return ResponseEntity.ok(HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return ResponseEntity.ok(HttpStatus.NO_CONTENT);
        }
    }

}
