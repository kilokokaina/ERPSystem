package com.work.erpsystem.service;

import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.CategoryModel;

import java.util.List;

public interface CategoryService {

    CategoryModel save(CategoryModel categoryModel);
    List<CategoryModel> findAll();
    CategoryModel findById(Long categoryModelId) throws NoDBRecord;
    CategoryModel findByName(String categoryModelName) throws NoDBRecord;
    void deleteById(Long categoryModelId) throws NoDBRecord;
    void delete(CategoryModel categoryModel) throws NoDBRecord;

}
