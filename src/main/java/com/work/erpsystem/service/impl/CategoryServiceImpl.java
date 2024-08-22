package com.work.erpsystem.service.impl;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.CategoryModel;
import com.work.erpsystem.model.OrganizationModel;
import com.work.erpsystem.repository.CategoryRepository;
import com.work.erpsystem.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryModel save(CategoryModel categoryModel) throws DuplicateDBRecord {
//        if (categoryRepository.findByCategoryName(categoryModel.getCategoryName()) != null) {
//            String exceptionMessage = "Record with name [%s] already exists in DB";
//            throw new DuplicateDBRecord(exceptionMessage);
//        }

        return categoryRepository.save(categoryModel);
    }

    @Override
    public CategoryModel update(CategoryModel category) throws NoDBRecord {
        if (Objects.isNull(this.findById(category.getCategoryId()))) {
            throw new NoDBRecord(String.format("No such record in data base with id: %d", category.getCategoryId()));
        }

        return categoryRepository.save(category);
    }

    @Override
    public List<CategoryModel> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public CategoryModel findById(Long categoryId) throws NoDBRecord {
        CategoryModel category = categoryRepository.findById(categoryId).orElse(null);

        if (Objects.isNull(category)) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, categoryId));
        }

        return category;
    }

    @Override
    public CategoryModel findByName(String categoryName) throws NoDBRecord {
        CategoryModel category = categoryRepository.findByCategoryName(categoryName);

        if (Objects.isNull(category)) {
            String exceptionMessage = "No such record in data base with name: %s";
            throw new NoDBRecord(String.format(exceptionMessage, categoryName));
        }

        return category;
    }

    @Override
    public List<CategoryModel> findByOrg(OrganizationModel organization) {
        return categoryRepository.findByOrganization(organization);
    }

    @Override
    public void deleteById(Long categoryId) throws NoDBRecord {
        CategoryModel category = this.findById(categoryId);

        if (Objects.isNull(category)) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, categoryId));
        }

        categoryRepository.deleteById(categoryId);

    }

    @Override
    public void delete(CategoryModel category) {
        categoryRepository.delete(category);
    }
}
