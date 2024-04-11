package com.work.erpsystem.api;

import com.work.erpsystem.dto.ItemDTO;
import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.CategoryModel;
import com.work.erpsystem.model.ItemModel;
import com.work.erpsystem.service.impl.CategoryServiceImpl;
import com.work.erpsystem.service.impl.ItemServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/item")
public class ItemAPI {

    private final ItemServiceImpl itemService;
    private final CategoryServiceImpl categoryService;

    @Autowired
    public ItemAPI(ItemServiceImpl itemService, CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<ItemModel>> findAll() {
        return ResponseEntity.ok(itemService.findAll());
    }

    //HttpStatus - 200 (OK), 204 (No record in DB)
    @GetMapping("{id}")
    public ResponseEntity<ItemModel> findById(@PathVariable(value = "id") Long itemModelId) {
        try {
            ItemModel itemModel = itemService.findById(itemModelId);
            return new ResponseEntity<>(itemModel, HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

    //HttpStatus - 200 (OK), 204 (No record in DB)
    @GetMapping("find_by_name")
    public ResponseEntity<ItemModel> findByName(@RequestParam(name = "item_name") String itemName) {
        try {
            ItemModel itemModel = itemService.findByName(itemName);
            return new ResponseEntity<>(itemModel, HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    //HttpStatus - 200 (OK), 409 (Duplicate record in DB)
    @PostMapping
    public ResponseEntity<ItemModel> addItem(@RequestBody ItemDTO itemDto) throws NoDBRecord, DuplicateDBRecord {
        ItemModel itemModel = new ItemModel();

        CategoryModel categoryModel = categoryService.findByName(itemDto.getCategoryName());

        itemModel.setItemName(itemDto.getItemName());
        itemModel.setCategoryModel(categoryModel);
        itemModel.setItemPurchasePrice(itemDto.getItemPurchasePrice());
        itemModel.setItemSalePrice(itemDto.getItemSalePrice());

        try {
            return ResponseEntity.ok(itemService.save(itemModel));
        } catch (DuplicateDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<ItemModel> updateItem(@PathVariable(value = "id") ItemModel itemModel,
                                                @RequestBody ItemDTO itemDto) throws NoDBRecord {
        CategoryModel categoryModel = categoryService.findByName(itemDto.getCategoryName());

        itemModel.setItemName(itemDto.getItemName());
        itemModel.setCategoryModel(categoryModel);
        itemModel.setItemCreationDate(new Date());
        itemModel.setItemPurchasePrice(itemDto.getItemPurchasePrice());
        itemModel.setItemSalePrice(itemDto.getItemSalePrice());

        return ResponseEntity.ok(itemService.update(itemModel));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> deleteItemById(@PathVariable(value = "id") Long itemId) {
        try {
            itemService.deleteById(itemId);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return ResponseEntity.ok(HttpStatus.NO_CONTENT);
        }
    }

}
