package com.work.erpsystem.api;

import com.work.erpsystem.dto.ItemQuantityDTO;
import com.work.erpsystem.dto.WarehouseDTO;
import com.work.erpsystem.exception.DBException;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.ItemModel;
import com.work.erpsystem.model.SaleModel;
import com.work.erpsystem.model.WarehouseModel;
import com.work.erpsystem.repository.SaleRepository;
import com.work.erpsystem.service.impl.ItemServiceImpl;
import com.work.erpsystem.service.impl.OrgServiceImpl;
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
    private final SaleRepository saleRepository;
    private final ItemServiceImpl itemService;
    private final OrgServiceImpl orgService;

    @Autowired
    public WarehouseAPI(WarehouseServiceImpl warehouseService, ItemServiceImpl itemService,
                        OrgServiceImpl orgService, SaleRepository saleRepository) {
        this.warehouseService = warehouseService;
        this.saleRepository = saleRepository;
        this.itemService = itemService;
        this.orgService = orgService;
    }

    @GetMapping
    public @ResponseBody ResponseEntity<List<WarehouseModel>> findAll(@PathVariable(value = "org_uuid") Long orgId,
                                                                      Authentication authentication) {
        try {
            return ResponseEntity.ok(warehouseService.findByOrganization(orgService.findById(orgId)));
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("{id}")
    public @ResponseBody ResponseEntity<WarehouseModel> findById(@PathVariable(value = "org_uuid") Long orgId,
                                                                 @PathVariable(value = "id") Long warehouseId,
                                                                 Authentication authentication) {
        try {
            WarehouseModel warehouse = warehouseService.findById(warehouseId);

            return ResponseEntity.ok(warehouse);
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping
    public @ResponseBody ResponseEntity<WarehouseModel> addWarehouse(@PathVariable(value = "org_uuid") Long orgId,
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
    public @ResponseBody ResponseEntity<WarehouseModel> updateWarehouse(@PathVariable(value = "org_uuid") Long orgId,
                                                                        @PathVariable(value = "id") Long warehouseId,
                                                                        @RequestBody WarehouseModel warehouseNew,
                                                                        Authentication authentication) {
        try {
            WarehouseModel warehouseModel = warehouseService.findById(warehouseId);

            warehouseModel.setWarehouseName(warehouseNew.getWarehouseName());
            warehouseModel.setWarehouseAddress(warehouseNew.getWarehouseAddress());

            return ResponseEntity.ok(warehouseService.update(warehouseModel));
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("add_items/{id}")
    public @ResponseBody ResponseEntity<WarehouseModel> addItemsToWarehouse(@PathVariable(value = "org_uuid") Long orgId,
                                                                            @PathVariable(value = "id") WarehouseModel warehouseModel,
                                                                            @RequestBody ItemQuantityDTO itemQuantityDTO,
                                                                            Authentication authentication) {
        try {
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
    public @ResponseBody ResponseEntity<WarehouseModel> addSalesToWarehouse(@PathVariable(value = "org_uuid") Long orgId,
                                                                            @PathVariable(value = "id") WarehouseModel warehouseModel,
                                                                            @RequestBody ItemQuantityDTO itemQuantityDTO,
                                                                            Authentication authentication) {
        try {
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
    public @ResponseBody ResponseEntity<HttpStatus> deleteWarehouse(@PathVariable(value = "org_uuid") Long orgId,
                                                                    @PathVariable(value = "id") Long warehouseId,
                                                                    Authentication authentication) {
        try {
            warehouseService.deleteById(warehouseId);

            return ResponseEntity.ok(HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return ResponseEntity.ok(HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping("delete_item/{id}")
    public @ResponseBody ResponseEntity<HttpStatus> deleteItemFromWarehouse(@PathVariable(value = "org_uuid") Long orgId,
                                                                            @PathVariable(value = "id") WarehouseModel warehouseModel,
                                                                            @RequestParam(value = "item_id") Long itemId,
                                                                            Authentication authentication) {
        try {
            ItemModel itemModel = itemService.findById(itemId);

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
