package com.work.erpsystem.api;

import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.CategoryModel;
import com.work.erpsystem.service.impl.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/category")
public class CategoryAPI {

    private final CategoryServiceImpl categoryService;

    @Autowired
    public CategoryAPI(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryModel>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<CategoryModel> findById(@PathVariable(value = "id") CategoryModel categoryModel) {
        return ResponseEntity.ok(categoryModel);
    }

    @PostMapping
    public ResponseEntity<CategoryModel> addCategory(@RequestBody CategoryModel categoryModel) {
        return ResponseEntity.ok(categoryService.save(categoryModel));
    }

    @PutMapping("{id}")
    public ResponseEntity<CategoryModel> updateCategory(@PathVariable(value = "id") CategoryModel categoryModel,
                                                        @RequestBody CategoryModel newCategoryModel) {
        categoryModel.setCategoryName(newCategoryModel.getCategoryName());
        return ResponseEntity.ok(categoryService.save(categoryModel));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable(value = "id") Long categoryId) throws NoDBRecord {
        categoryService.deleteById(categoryId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
