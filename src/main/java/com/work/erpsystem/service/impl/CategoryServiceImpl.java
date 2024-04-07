package com.work.erpsystem.service.impl;

import com.work.erpsystem.model.CategoryModel;
import com.work.erpsystem.repository.CategoryRepository;
import com.work.erpsystem.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryModel save(CategoryModel categoryModel) {
        return categoryRepository.save(categoryModel);
    }

    @Override
    public CategoryModel findById(Long categoryModelId) {
        return categoryRepository.findById(categoryModelId).orElse(null);
    }

    @Override
    public CategoryModel findByName(String categoryModelName) {
        return categoryRepository.findByCategoryName(categoryModelName);
    }

    @Override
    public List<CategoryModel> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public CategoryModel deleteById(Long categoryModelId) {
        CategoryModel categoryModel = this.findById(categoryModelId);
        categoryRepository.deleteById(categoryModelId);

        return categoryModel;
    }

    @Override
    public CategoryModel delete(CategoryModel categoryModel) {
        categoryRepository.delete(categoryModel);
        return categoryModel;
    }
}
