package com.work.erpsystem.api;

import com.work.erpsystem.dto.ItemQuantityDTO;
import com.work.erpsystem.dto.TransitDTO;
import com.work.erpsystem.dto.WarehouseDTO;
import com.work.erpsystem.exception.DBException;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.*;
import com.work.erpsystem.repository.SaleRepository;
import com.work.erpsystem.repository.TransitRepository;
import com.work.erpsystem.service.impl.ItemServiceImpl;
import com.work.erpsystem.service.impl.OrgServiceImpl;
import com.work.erpsystem.service.impl.WarehouseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("{org_uuid}/api/warehouse")
public class WarehouseAPI {

    private final WarehouseServiceImpl warehouseService;
    private final TransitRepository transitRepository;
    private final SaleRepository saleRepository;
    private final ItemServiceImpl itemService;
    private final OrgServiceImpl orgService;

    @Autowired
    public WarehouseAPI(WarehouseServiceImpl warehouseService, TransitRepository transitRepository,
                        ItemServiceImpl itemService, OrgServiceImpl orgService,
                        SaleRepository saleRepository) {
        this.warehouseService = warehouseService;
        this.transitRepository = transitRepository;
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

    @GetMapping("get_transit")
    public @ResponseBody ResponseEntity<TransitModel> getTransit(@PathVariable(value = "org_uuid") Long orgId,
                                                                 @RequestParam(value = "transitId") Long transitId,
                                                                 Authentication authentication) {
        return ResponseEntity.ok(transitRepository.findById(transitId).orElse(null));
    }

    @PostMapping("add_transit")
    public @ResponseBody ResponseEntity<TransitModel> addNewTransit(@PathVariable(value = "org_uuid") Long orgId,
                                                              @RequestBody TransitDTO transitDto,
                                                              Authentication authentication) {
        TransitModel transit = new TransitModel();
        transit.setTransitStatus(String.valueOf(TransitStatus.valueOf(transitDto.getStatus())));

        try {
            WarehouseModel departWarehouse = warehouseService.findById(transitDto.getDepartPoint());
            WarehouseModel arriveWarehouse = warehouseService.findById(transitDto.getArrivePoint());

            Map<ItemModel, Integer> transitItemQuantity = new HashMap<>();
            for (ItemQuantityDTO itemDto : transitDto.getItems()) {
                ItemModel transitItem = itemService.findById(itemDto.getItemId());

                if (departWarehouse.getItemQuantity().get(transitItem) < itemDto.getQuantity()) {
                    log.error("Items quantity conflict: requested quantity is more than WH got");
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                transitItemQuantity.put(transitItem, itemDto.getQuantity());
            }
            transit.setItemQuantity(transitItemQuantity);

            transit.setDepartPoint(departWarehouse);
            transit.setArrivePoint(arriveWarehouse);
            transit.setOrganization(orgService.findById(transitDto.getOrgId()));

        } catch (NoDBRecord e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        transitRepository.save(transit);

        return ResponseEntity.ok(transit);
    }

    @PutMapping("change_transit_status")
    public @ResponseBody ResponseEntity<TransitModel> changeTransitStatus(@PathVariable(value = "org_uuid") Long orgId,
                                                                          @RequestParam Long transitId, @RequestParam String transitStatus,
                                                                          Authentication authentication) throws NoDBRecord {
        TransitModel transit = transitRepository.findById(transitId).orElse(null);

        if (transit == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        transit.setTransitStatus(String.valueOf(TransitStatus.valueOf(transitStatus)));
        Map<ItemModel, Integer> transitItems = transit.getItemQuantity();

        try {
            switch (transitStatus) {
                case "IN_TRANSIT" -> {
                    WarehouseModel warehouseModel = transit.getDepartPoint();
                    Map<ItemModel, Integer> warehouseItems = warehouseModel.getItemQuantity();

                    for (Map.Entry<ItemModel, Integer> entry : transitItems.entrySet()) {
                        int currentQuantity = warehouseItems.get(entry.getKey());
                        warehouseItems.replace(entry.getKey(), currentQuantity - entry.getValue());
                    }

                    warehouseService.update(warehouseModel);
                }
                case "DELIVERED" -> {
                    WarehouseModel warehouseModel = transit.getArrivePoint();
                    Map<ItemModel, Integer> warehouseItems = warehouseModel.getItemQuantity();

                    for (Map.Entry<ItemModel, Integer> entry : transitItems.entrySet()) {
                        int currentQuantity = warehouseItems.get(entry.getKey());
                        warehouseItems.replace(entry.getKey(), currentQuantity + entry.getValue());
                    }

                    warehouseService.update(warehouseModel);
                }
            }

            transitRepository.save(transit);

        } catch (NoDBRecord exception) {
            log.error(exception.getMessage());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(transit);
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
