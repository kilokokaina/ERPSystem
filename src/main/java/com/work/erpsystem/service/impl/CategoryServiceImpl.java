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
        if (categoryRepository.findByCategoryName(categoryModel.getCategoryName()) != null) {
            String exceptionMessage = "Record with name [%s] already exists in DB";
            throw new DuplicateDBRecord(exceptionMessage);
        }

        return categoryRepository.save(categoryModel);
    }

    @Override
    public CategoryModel update(CategoryModel categoryModel) {
        return categoryRepository.save(categoryModel);
    }

    @Override
    public List<CategoryModel> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public CategoryModel findById(Long categoryModelId) throws NoDBRecord {
        CategoryModel categoryModel = categoryRepository.findById(categoryModelId).orElse(null);

        if (Objects.isNull(categoryModel)) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, categoryModelId));
        }

        return categoryModel;
    }

    @Override
    public CategoryModel findByName(String categoryModelName) throws NoDBRecord {
        CategoryModel categoryModel = categoryRepository.findByCategoryName(categoryModelName);

        if (Objects.isNull(categoryModel)) {
            String exceptionMessage = "No such record in data base with name: %s";
            throw new NoDBRecord(String.format(exceptionMessage, categoryModelName));
        }

        return categoryModel;
    }

    @Override
    public List<CategoryModel> findByOrg(OrganizationModel organizationModel) {
        return categoryRepository.findByOrganization(organizationModel);
    }

    @Override
    public void deleteById(Long categoryModelId) throws NoDBRecord {
        CategoryModel categoryModel = this.findById(categoryModelId);

        if (Objects.isNull(categoryModel)) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, categoryModelId));
        }

        categoryRepository.deleteById(categoryModelId);

    }

    @Override
    public void delete(CategoryModel categoryModel) {
        categoryRepository.delete(categoryModel);
    }
}
