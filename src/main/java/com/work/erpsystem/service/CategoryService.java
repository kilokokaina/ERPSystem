package com.work.erpsystem.service;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.CategoryModel;
import com.work.erpsystem.model.OrganizationModel;

import java.util.List;

public interface CategoryService {

    CategoryModel save(CategoryModel category) throws DuplicateDBRecord;
    CategoryModel update(CategoryModel category) throws NoDBRecord;
    List<CategoryModel> findAll();
    CategoryModel findById(Long categoryId) throws NoDBRecord;
    CategoryModel findByName(String categoryName) throws NoDBRecord;
    List<CategoryModel> findByOrg(OrganizationModel organization);
    void deleteById(Long categoryId) throws NoDBRecord;
    void delete(CategoryModel category) throws NoDBRecord;

}
