package com.work.erpsystem.api;

import com.work.erpsystem.dto.ItemDTO;
import com.work.erpsystem.exception.DBException;
import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.BarcodeModel;
import com.work.erpsystem.model.CategoryModel;
import com.work.erpsystem.model.FileModel;
import com.work.erpsystem.model.ItemModel;
import com.work.erpsystem.repository.FileRepository;
import com.work.erpsystem.service.impl.BarcodeServiceImpl;
import com.work.erpsystem.service.impl.CategoryServiceImpl;
import com.work.erpsystem.service.impl.ItemServiceImpl;
import com.work.erpsystem.service.impl.OrgServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;

@Slf4j
@RestController
@RequestMapping("{org_uuid}/api/item")
public class ItemAPI {

    private final ItemServiceImpl itemService;
    private final BarcodeServiceImpl barcodeService;
    private final CategoryServiceImpl categoryService;
    private final FileRepository fileRepository;
    private final OrgServiceImpl orgService;

    @Autowired
    public ItemAPI(ItemServiceImpl itemService, CategoryServiceImpl categoryService,
                   BarcodeServiceImpl barcodeService, OrgServiceImpl orgService,
                   FileRepository fileRepository) {
        this.barcodeService = barcodeService;
        this.categoryService = categoryService;
        this.fileRepository = fileRepository;
        this.itemService = itemService;
        this.orgService = orgService;
    }

    @GetMapping
    public ResponseEntity<List<ItemModel>> findAll(@PathVariable(value = "org_uuid") Long orgId) {
        return ResponseEntity.ok(itemService.findAll());
    }

    @GetMapping("{id}")
    public @ResponseBody ResponseEntity<ItemModel> findById(@PathVariable(value = "org_uuid") Long orgId,
                                                            @PathVariable(value = "id") Long itemId) {
        try {
            ItemModel item = itemService.findById(itemId);
            return new ResponseEntity<>(item, HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

    @GetMapping("find_by_name")
    public @ResponseBody ResponseEntity<ItemModel> findByName(@PathVariable(value = "org_uuid") Long orgId,
                                                              @RequestParam(name = "item_name") String itemName) {
        try {
            ItemModel item = itemService.findByName(itemName);
            return new ResponseEntity<>(item, HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("find_by_category")
    public @ResponseBody ResponseEntity<List<ItemModel>> findByCategory(@PathVariable(value = "org_uuid") Long orgId,
                                                                        @RequestParam(name = "category_name") String categoryName) {
        try {
            CategoryModel category = categoryService.findByName(categoryName);
            return ResponseEntity.ok(itemService.findByCategoryId(category.getCategoryId()));
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("find_by_barcode")
    public @ResponseBody ResponseEntity<ItemModel> findByBarcode(@PathVariable(value = "org_uuid") Long orgId,
                                                                 @RequestParam(name = "barcode") String code) {
        try {
            BarcodeModel barcode = barcodeService.findByCode(code);
            return ResponseEntity.ok(itemService.findByBarcode(barcode));
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping
    public @ResponseBody ResponseEntity<ItemModel> addItem(@RequestBody ItemDTO itemDto,
                                                           @PathVariable(value = "org_uuid") Long orgId) {
        try {
            ItemModel item = new ItemModel();

            CategoryModel category = categoryService.findByName(itemDto.getCategoryName());

            item.setItemName(itemDto.getItemName());
            item.setCategoryModel(category);
            item.setItemDescribe(itemDto.getItemDescribe());
            item.setItemPurchasePrice(itemDto.getItemPurchasePrice());
            item.setOrganizationModel(orgService.findById(orgId));

            return ResponseEntity.ok(itemService.save(item));
        } catch (DBException exception) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("{id}")
    public @ResponseBody ResponseEntity<ItemModel> updateItem(@RequestBody ItemDTO itemDto,
                                                              @PathVariable(value = "id") ItemModel item,
                                                              @PathVariable(value = "org_uuid") Long orgId) {
        try {
            item.setItemName(itemDto.getItemName());
            item.setItemDescribe(itemDto.getItemDescribe());
            item.setItemPurchasePrice(itemDto.getItemPurchasePrice());

            return ResponseEntity.ok(itemService.update(item));
        } catch (DBException exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping("{id}")
    public @ResponseBody ResponseEntity<HttpStatus> deleteItemById(@PathVariable(value = "org_uuid") Long orgId,
                                                                   @PathVariable(value = "id") Long itemId) {
        try {
            ItemModel item = itemService.findById(itemId);
            List<FileModel> imageList = item.getItemImages();
            item.setItemImages(new ArrayList<>());

            fileRepository.deleteAll(imageList);
            for (FileModel image : imageList) Files.delete(Path.of(image.getFilePath()));
            itemService.deleteById(itemId);

            return ResponseEntity.ok(HttpStatus.OK);
        } catch (NoDBRecord | IOException exception) {
            log.error(exception.getMessage());
            return ResponseEntity.ok(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("add_barcode/{id}")
    public @ResponseBody ResponseEntity<BarcodeModel> addBarcode(@PathVariable(value = "org_uuid") Long orgId,
                                                                 @PathVariable(value = "id") Long itemId,
                                                                 @RequestBody BarcodeModel barcode) {
        try {
            ItemModel item = itemService.findById(itemId);

            barcodeService.save(barcode);
            item.setBarcode(barcode);
            itemService.update(item);

            return ResponseEntity.ok(barcode);
        } catch (NoDBRecord | DuplicateDBRecord exception) {
            log.error(exception.getMessage());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

}
