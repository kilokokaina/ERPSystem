package com.work.erpsystem.repository;

import com.work.erpsystem.model.CategoryModel;
import com.work.erpsystem.model.OrganizationModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryModel, Long> {

    CategoryModel findByCategoryName(String categoryName);
    List<CategoryModel> findByOrganization(OrganizationModel organization);

}
