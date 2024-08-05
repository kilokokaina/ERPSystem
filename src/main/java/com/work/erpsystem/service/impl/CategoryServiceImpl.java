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
    public CategoryModel update(CategoryModel categoryModel) throws NoDBRecord {
        if (Objects.isNull(this.findById(categoryModel.getCategoryId()))) {
            throw new NoDBRecord(String.format("No such record in data base with id: %d", categoryModel.getCategoryId()));
        }

        return categoryRepository.save(categoryModel);
    }

    @Override
    public List<CategoryModel> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public CategoryModel findById(Long categoryId) throws NoDBRecord {
        CategoryModel categoryModel = categoryRepository.findById(categoryId).orElse(null);

        if (Objects.isNull(categoryModel)) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, categoryId));
        }

        return categoryModel;
    }

    @Override
    public CategoryModel findByName(String categoryName) throws NoDBRecord {
        CategoryModel categoryModel = categoryRepository.findByCategoryName(categoryName);

        if (Objects.isNull(categoryModel)) {
            String exceptionMessage = "No such record in data base with name: %s";
            throw new NoDBRecord(String.format(exceptionMessage, categoryName));
        }

        return categoryModel;
    }

    @Override
    public List<CategoryModel> findByOrg(OrganizationModel organizationModel) {
        return categoryRepository.findByOrganization(organizationModel);
    }

    @Override
    public void deleteById(Long categoryId) throws NoDBRecord {
        CategoryModel categoryModel = this.findById(categoryId);

        if (Objects.isNull(categoryModel)) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, categoryId));
        }

        categoryRepository.deleteById(categoryId);

    }

    @Override
    public void delete(CategoryModel categoryModel) {
        categoryRepository.delete(categoryModel);
    }
}
