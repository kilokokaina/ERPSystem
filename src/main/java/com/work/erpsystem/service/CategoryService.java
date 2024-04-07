package com.work.erpsystem.service;

import com.work.erpsystem.model.CategoryModel;

import java.util.List;

public interface CategoryService {

    CategoryModel save(CategoryModel categoryModel);
    CategoryModel findById(Long categoryModelId);
    CategoryModel findByName(String categoryModelName);
    List<CategoryModel> findAll();
    CategoryModel deleteById(Long categoryModelId);
    CategoryModel delete(CategoryModel categoryModel);

}
