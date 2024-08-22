package com.work.erpsystem.api;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.CategoryModel;
import com.work.erpsystem.service.impl.CategoryServiceImpl;
import com.work.erpsystem.service.impl.OrgServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("{org_uuid}/api/category")
public class CategoryAPI {

    private final CategoryServiceImpl categoryService;
    private final OrgServiceImpl orgService;

    @Autowired
    public CategoryAPI(CategoryServiceImpl categoryService, OrgServiceImpl orgService) {
        this.categoryService = categoryService;
        this.orgService = orgService;
    }

    @GetMapping
    public @ResponseBody ResponseEntity<List<CategoryModel>> findAll(@PathVariable(value = "org_uuid") Long orgId) {
        try {
            return ResponseEntity.ok(categoryService.findByOrg(orgService.findById(orgId)));
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("{id}")
    public @ResponseBody ResponseEntity<CategoryModel> findById(@PathVariable(value = "id") Long categoryId,
                                                                @PathVariable(value = "org_uuid") Long orgId,
                                                                Authentication authentication) {
        try {
            CategoryModel category = categoryService.findById(categoryId);
            return ResponseEntity.ok(category);
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping
    public @ResponseBody ResponseEntity<CategoryModel> addCategory(@PathVariable(value = "org_uuid") Long orgId,
                                                                   @RequestBody CategoryModel category) {
        try {
            category.setOrganization(orgService.findById(orgId));

            return ResponseEntity.ok(categoryService.save(category));
        } catch (DuplicateDBRecord | NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("{id}")
    public @ResponseBody ResponseEntity<CategoryModel> updateCategory(@PathVariable(value = "id") Long categoryId,
                                                                      @PathVariable(value = "org_uuid") Long orgId,
                                                                      @RequestBody CategoryModel newCategory,
                                                                      Authentication authentication) {
        try {
            CategoryModel category = categoryService.findById(categoryId);
            category.setCategoryName(newCategory.getCategoryName());

            return ResponseEntity.ok(categoryService.update(category));
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping("{id}")
    public @ResponseBody ResponseEntity<HttpStatus> deleteCategory(@PathVariable(value = "id") Long categoryId,
                                                                   @PathVariable(value = "org_uuid") Long orgId,
                                                                   Authentication authentication) {
        try {
            categoryService.deleteById(categoryId);

            return ResponseEntity.ok(HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return ResponseEntity.ok(HttpStatus.NO_CONTENT);
        }
    }

}
