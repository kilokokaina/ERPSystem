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
                                                            @PathVariable(value = "id") Long itemModelId) {
        try {
            ItemModel itemModel = itemService.findById(itemModelId);
            return new ResponseEntity<>(itemModel, HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

    @GetMapping("find_by_name")
    public @ResponseBody ResponseEntity<ItemModel> findByName(@PathVariable(value = "org_uuid") Long orgId,
                                                              @RequestParam(name = "item_name") String itemName) {
        try {
            ItemModel itemModel = itemService.findByName(itemName);
            return new ResponseEntity<>(itemModel, HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("find_by_category")
    public @ResponseBody ResponseEntity<List<ItemModel>> findByCategory(@PathVariable(value = "org_uuid") Long orgId,
                                                                        @RequestParam(name = "category_name") String categoryName) {
        try {
            CategoryModel categoryModel = categoryService.findByName(categoryName);
            return ResponseEntity.ok(itemService.findByCategoryId(categoryModel.getCategoryId()));
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("find_by_barcode")
    public @ResponseBody ResponseEntity<ItemModel> findByBarcode(@PathVariable(value = "org_uuid") Long orgId,
                                                                 @RequestParam(name = "barcode") String barcode) {
        try {
            BarcodeModel barcodeModel = barcodeService.findByCode(barcode);
            return ResponseEntity.ok(itemService.findByBarcode(barcodeModel));
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping
    public @ResponseBody ResponseEntity<ItemModel> addItem(@RequestBody ItemDTO itemDto,
                                                           @PathVariable(value = "org_uuid") Long orgId) {
        try {
            ItemModel itemModel = new ItemModel();

            CategoryModel categoryModel = categoryService.findByName(itemDto.getCategoryName());

            itemModel.setItemName(itemDto.getItemName());
            itemModel.setCategoryModel(categoryModel);
            itemModel.setItemPurchasePrice(itemDto.getItemPurchasePrice());
            itemModel.setOrganizationModel(orgService.findById(orgId));

            return ResponseEntity.ok(itemService.save(itemModel));
        } catch (DBException exception) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("{id}")
    public @ResponseBody ResponseEntity<ItemModel> updateItem(@RequestBody ItemDTO itemDto,
                                                              @PathVariable(value = "id") ItemModel itemModel,
                                                              @PathVariable(value = "org_uuid") Long orgId) {
        try {
            CategoryModel categoryModel = categoryService.findByName(itemDto.getCategoryName());

            itemModel.setItemName(itemDto.getItemName());
            itemModel.setCategoryModel(categoryModel);
            itemModel.setItemCreationDate(new Date());
            itemModel.setItemPurchasePrice(itemDto.getItemPurchasePrice());

            return ResponseEntity.ok(itemService.update(itemModel));
        } catch (DBException exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping("{id}")
    public @ResponseBody ResponseEntity<HttpStatus> deleteItemById(@PathVariable(value = "org_uuid") Long orgId,
                                                                   @PathVariable(value = "id") Long itemId) {
        try {
            ItemModel itemModel = itemService.findById(itemId);
            List<FileModel> imageList = itemModel.getItemImages();
            itemModel.setItemImages(new ArrayList<>());

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
            ItemModel itemModel = itemService.findById(itemId);

            barcodeService.save(barcode);
            itemModel.setBarcode(barcode);
            itemService.update(itemModel);

            return ResponseEntity.ok(barcode);
        } catch (NoDBRecord | DuplicateDBRecord exception) {
            log.error(exception.getMessage());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

}
