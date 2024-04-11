package com.work.erpsystem.api;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.CategoryModel;
import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.model.WarehouseModel;
import com.work.erpsystem.service.impl.CategoryServiceImpl;
import com.work.erpsystem.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/category")
public class CategoryAPI {

    private final CategoryServiceImpl categoryService;
    private final UserServiceImpl userService;

    @Autowired
    public CategoryAPI(CategoryServiceImpl categoryService, UserServiceImpl userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryModel>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<CategoryModel> findById(@PathVariable(value = "id") Long categoryId,
                                                  Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());

        try {
            CategoryModel categoryModel = categoryService.findById(categoryId);

            if (!categoryModel.getOrganization().equals(userModel.getOrgEmployee())) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            return ResponseEntity.ok(categoryModel);

        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping
    public ResponseEntity<CategoryModel> addCategory(@RequestBody CategoryModel categoryModel, Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());
        categoryModel.setOrganization(userModel.getOrgEmployee());

        try {
            return ResponseEntity.ok(categoryService.save(categoryModel));
        } catch (DuplicateDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<CategoryModel> updateCategory(@PathVariable(value = "id") Long categoryId,
                                                        @RequestBody CategoryModel categoryNew,
                                                        Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());

        try {
            CategoryModel categoryModel = categoryService.findById(categoryId);

            if (!categoryModel.getOrganization().equals(userModel.getOrgEmployee())) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            categoryModel.setCategoryName(categoryNew.getCategoryName());
            return ResponseEntity.ok(categoryService.update(categoryModel));
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable(value = "id") Long categoryId,
                                                     Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());

        try {
            CategoryModel categoryModel = categoryService.findById(categoryId);

            if (!categoryModel.getOrganization().equals(userModel.getOrgEmployee())) {
                return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
            }

            categoryService.deleteById(categoryId);

            return ResponseEntity.ok(HttpStatus.OK);

        } catch (NoDBRecord exception) {
            return ResponseEntity.ok(HttpStatus.NO_CONTENT);
        }
    }

}
