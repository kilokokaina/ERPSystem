package com.work.erpsystem.api;

import com.work.erpsystem.dto.ItemDTO;
import com.work.erpsystem.model.CategoryModel;
import com.work.erpsystem.model.ItemModel;
import com.work.erpsystem.service.impl.CategoryServiceImpl;
import com.work.erpsystem.service.impl.ItemServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

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

    @GetMapping("{id}")
    public ResponseEntity<ItemModel> findById(@PathVariable(value = "id") Long itemModelId) {
        return ResponseEntity.ok(itemService.findById(itemModelId));
    }

    @PostMapping
    public ResponseEntity<ItemModel> addItem(@RequestBody ItemDTO itemDto) {
        ItemModel itemModel = new ItemModel();
        CategoryModel categoryModel = categoryService.findByName(itemDto.getCategoryName());

        itemModel.setItemName(itemDto.getItemName());
        itemModel.setCategoryModel(categoryModel);
        itemModel.setItemPurchasePrice(itemDto.getItemPurchasePrice());
        itemModel.setItemSalePrice(itemDto.getItemSalePrice());

        return ResponseEntity.ok(itemService.save(itemModel));
    }

    @PutMapping("{id}")
    public ResponseEntity<ItemModel> changeItem(@PathVariable(value = "id") ItemModel itemModel,
                                                @RequestBody ItemDTO itemDto) {
        CategoryModel categoryModel = categoryService.findByName(itemDto.getCategoryName());

        itemModel.setItemName(itemDto.getItemName());
        itemModel.setCategoryModel(categoryModel);
        itemModel.setItemCreationDate(new Date());
        itemModel.setItemPurchasePrice(itemDto.getItemPurchasePrice());
        itemModel.setItemSalePrice(itemDto.getItemSalePrice());

        return ResponseEntity.ok(itemService.save(itemModel));
    }

}
