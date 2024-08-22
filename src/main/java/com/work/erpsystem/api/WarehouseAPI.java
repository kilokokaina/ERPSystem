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
import java.util.function.UnaryOperator;

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

    @GetMapping("get_item_price/{id}")
    public @ResponseBody ResponseEntity<Double> getItemPrice(@PathVariable(value = "org_uuid") Long orgId,
                                                              @PathVariable(value = "id") Long warehouseId,
                                                              @RequestParam(value = "item_id") Long itemId,
                                                              Authentication authentication) {
        try {
            WarehouseModel warehouse = warehouseService.findById(warehouseId);
            ItemModel item = itemService.findById(itemId);

            Map<ItemModel, Double> itemPrice = warehouse.getItemPrice();

            return ResponseEntity.ok(itemPrice.get(item));
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping
    public @ResponseBody ResponseEntity<WarehouseModel> addWarehouse(@PathVariable(value = "org_uuid") Long orgId,
                                                                     @RequestBody WarehouseDTO warehouseDto) {
        WarehouseModel warehouse = new WarehouseModel();

        try {
            warehouse.setOrganization(orgService.findById(orgId));
            warehouse.setWarehouseName(warehouseDto.getWarehouseName());
            warehouse.setWarehouseAddress(warehouseDto.getWarehouseAddress());

            return ResponseEntity.ok(warehouseService.save(warehouse));
        } catch (DBException exception) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("{id}")
    public @ResponseBody ResponseEntity<WarehouseModel> updateWarehouse(@PathVariable(value = "org_uuid") Long orgId,
                                                                        @PathVariable(value = "id") Long warehouseId,
                                                                        @RequestBody WarehouseModel newWarehouse,
                                                                        Authentication authentication) {
        try {
            WarehouseModel warehouse = warehouseService.findById(warehouseId);

            warehouse.setWarehouseName(newWarehouse.getWarehouseName());
            warehouse.setWarehouseAddress(newWarehouse.getWarehouseAddress());

            return ResponseEntity.ok(warehouseService.update(warehouse));
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("add_items/{id}")
    public @ResponseBody ResponseEntity<WarehouseModel> addItemsToWarehouse(@PathVariable(value = "org_uuid") Long orgId,
                                                                            @PathVariable(value = "id") WarehouseModel warehouse,
                                                                            @RequestBody ItemQuantityDTO itemQuantityDTO,
                                                                            Authentication authentication) {
        try {
            ItemModel item = itemService.findByName(itemQuantityDTO.getItemName());

            Map<ItemModel, Integer> itemQuantity = warehouse.getItemQuantity();
            Map<ItemModel, Double> itemPrice = warehouse.getItemPrice();

            double itemPriceValue = itemQuantityDTO.getItemPrice();
            itemPrice.put(item, itemPriceValue);

            try {
                int currentItemQuantity = itemQuantity.get(item);
                itemQuantity.put(item, itemQuantityDTO.getQuantity() + currentItemQuantity);
            } catch (NullPointerException exception) {
                itemQuantity.put(item, itemQuantityDTO.getQuantity());
            }

            warehouse.setItemQuantity(itemQuantity);
            warehouse.setItemPrice(itemPrice);

            return ResponseEntity.ok(warehouseService.update(warehouse));

        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("add_sales/{id}")
    public @ResponseBody ResponseEntity<SaleModel> addSalesToWarehouse(@PathVariable(value = "org_uuid") Long orgId,
                                                                            @PathVariable(value = "id") WarehouseModel warehouse,
                                                                            @RequestBody ItemQuantityDTO itemQuantityDTO,
                                                                            Authentication authentication) {
        try {
            SaleModel sale = new SaleModel();
            ItemModel item = itemService.findByName(itemQuantityDTO.getItemName());

            sale.setWarehouse(warehouse);
            sale.setItem(item);
            sale.setItemSalePrice(warehouse.getItemPrice().get(item));
            sale.setItemSaleQuantity(itemQuantityDTO.getQuantity());

            if (itemQuantityDTO.getQuantity() > warehouse.getItemQuantity().get(item)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                Map<ItemModel, Integer> itemQuantity = warehouse.getItemQuantity();

                int currentQuantity = itemQuantity.get(item);
                itemQuantity.put(item, currentQuantity - itemQuantityDTO.getQuantity());
                warehouse.setItemQuantity(itemQuantity);

                warehouseService.update(warehouse);
            }

            return ResponseEntity.ok(saleRepository.save(sale));
        } catch (NoDBRecord exception) {
            log.error(exception.getMessage());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
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
            Map<ItemModel, Double> transitItemPrice = new HashMap<>();

            for (ItemQuantityDTO itemDto : transitDto.getItems()) {
                ItemModel transitItem = itemService.findById(itemDto.getItemId());

                if (departWarehouse.getItemQuantity().get(transitItem) < itemDto.getQuantity()) {
                    log.error("Items quantity conflict: requested quantity is more than WH got");
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                transitItemQuantity.put(transitItem, itemDto.getQuantity());
                transitItemPrice.put(transitItem, itemDto.getItemPrice());
            }

            transit.setOrganization(orgService.findById(transitDto.getOrgId()));
            transit.setItemQuantity(transitItemQuantity);
            transit.setItemPrice(transitItemPrice);
            transit.setDepartPoint(departWarehouse);
            transit.setArrivePoint(arriveWarehouse);

        } catch (NoDBRecord e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        transitRepository.save(transit);

        return ResponseEntity.ok(transit);
    }

    @PutMapping("change_transit_status")
    public @ResponseBody ResponseEntity<TransitModel> changeTransitStatus(@PathVariable(value = "org_uuid") Long orgId,
                                                                          @RequestParam Long transitId, @RequestParam String transitStatus,
                                                                          Authentication authentication) {
        TransitModel transit = transitRepository.findById(transitId).orElse(null);

        if (transit == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        if (transit.getTransitStatus().equals(transitStatus)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Map<ItemModel, Integer> transitItems = transit.getItemQuantity();

        try {
            switch (transitStatus) {
                case "IN_TRANSIT" -> {
                    if (!transit.getTransitStatus().equals("CREATED")) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }

                    WarehouseModel warehouse = transit.getDepartPoint();
                    Map<ItemModel, Integer> warehouseItems = warehouse.getItemQuantity();

                    for (Map.Entry<ItemModel, Integer> entry : transitItems.entrySet()) {
                        int currentQuantity = warehouseItems.get(entry.getKey());
                        warehouseItems.replace(entry.getKey(), currentQuantity - entry.getValue());
                    }

                    warehouseService.update(warehouse);
                }
                case "DELIVERED" -> {
                    if (!transit.getTransitStatus().equals("IN_TRANSIT")) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }

                    WarehouseModel arriveWarehouse = transit.getArrivePoint();
                    Map<ItemModel, Integer> warehouseItems = arriveWarehouse.getItemQuantity();
                    Map<ItemModel, Double> warehousePrice = arriveWarehouse.getItemPrice();

                    for (Map.Entry<ItemModel, Integer> entry : transitItems.entrySet()) {
                        if (warehouseItems.containsKey(entry.getKey())) {
                            int currentQuantity = warehouseItems.get(entry.getKey());

                            warehouseItems.replace(entry.getKey(), currentQuantity + entry.getValue());
                            warehousePrice.replace(entry.getKey(), transit.getItemPrice().get(entry.getKey()));

                            arriveWarehouse.setItemQuantity(warehouseItems);
                            arriveWarehouse.setItemPrice(warehousePrice);
                        } else {
                            Map<ItemModel, Double> itemPrice = arriveWarehouse.getItemPrice();

                            itemPrice.put(entry.getKey(), transit.getItemPrice().get(entry.getKey()));
                            warehouseItems.put(entry.getKey(), entry.getValue());

                            arriveWarehouse.setItemQuantity(warehouseItems);
                            arriveWarehouse.setItemPrice(itemPrice);
                        }
                    }

                    warehouseService.update(arriveWarehouse);
                }

                default -> {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            transit.setTransitStatus(String.valueOf(TransitStatus.valueOf(transitStatus)));

            transitRepository.save(transit);

        } catch (NoDBRecord exception) {
            log.error(exception.getMessage());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(transit);
    }

    @DeleteMapping("delete_transit/{id}")
    public @ResponseBody ResponseEntity<HttpStatus> deleteTransit(@PathVariable(value = "org_uuid") Long orgId,
                                                                  @PathVariable(value = "id") Long warehouseId) {
        transitRepository.deleteById(warehouseId);
        return ResponseEntity.ok(HttpStatus.OK);
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
                                                                            @PathVariable(value = "id") WarehouseModel warehouse,
                                                                            @RequestParam(value = "item_id") Long itemId,
                                                                            Authentication authentication) {
        try {
            ItemModel item = itemService.findById(itemId);

            Map<ItemModel, Integer> itemQuantity = warehouse.getItemQuantity();
            itemQuantity.remove(item);
            warehouse.setItemQuantity(itemQuantity);

            warehouseService.update(warehouse);

            return ResponseEntity.ok(HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return ResponseEntity.ok(HttpStatus.NO_CONTENT);
        }
    }

}
