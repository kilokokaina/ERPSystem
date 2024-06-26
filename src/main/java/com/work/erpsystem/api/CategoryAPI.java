package com.work.erpsystem.api;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.CategoryModel;
import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.service.impl.CategoryServiceImpl;
import com.work.erpsystem.service.impl.OrgServiceImpl;
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
@RequestMapping("{org_uuid}/api/category")
public class CategoryAPI {

    private final CategoryServiceImpl categoryService;
    private final UserServiceImpl userService;
    private final OrgServiceImpl orgService;

    @Autowired
    public CategoryAPI(CategoryServiceImpl categoryService, UserServiceImpl userService,
                       OrgServiceImpl orgService) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.orgService = orgService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryModel>> findAll(@PathVariable(value = "org_uuid") Long orgId) {
        try {
            return ResponseEntity.ok(categoryService.findByOrg(orgService.findById(orgId)));
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<CategoryModel> findById(@PathVariable(value = "id") Long categoryId,
                                                  @PathVariable(value = "org_uuid") Long orgId,
                                                  Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());
        try {
            CategoryModel categoryModel = categoryService.findById(categoryId);

            if (!userModel.getOrgRole().containsKey(categoryModel.getOrganization())) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            return ResponseEntity.ok(categoryModel);
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping
    public ResponseEntity<CategoryModel> addCategory(@PathVariable(value = "org_uuid") Long orgId,
                                                     @RequestBody CategoryModel categoryModel) {
        try {
            categoryModel.setOrganization(orgService.findById(orgId));
            return ResponseEntity.ok(categoryService.save(categoryModel));
        } catch (DuplicateDBRecord | NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<CategoryModel> updateCategory(@PathVariable(value = "id") Long categoryId,
                                                        @PathVariable(value = "org_uuid") Long orgId,
                                                        @RequestBody CategoryModel categoryNew,
                                                        Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());
        try {
            CategoryModel categoryModel = categoryService.findById(categoryId);

            if (!userModel.getOrgRole().containsKey(orgService.findById(orgId))) {
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
                                                     @PathVariable(value = "org_uuid") Long orgId,
                                                     Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());
        try {
            if (!userModel.getOrgRole().containsKey(orgService.findById(orgId))) {
                return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
            }

            categoryService.deleteById(categoryId);

            return ResponseEntity.ok(HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return ResponseEntity.ok(HttpStatus.NO_CONTENT);
        }
    }

}
