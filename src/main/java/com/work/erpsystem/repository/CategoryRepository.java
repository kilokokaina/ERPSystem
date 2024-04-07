package com.work.erpsystem.repository;

import com.work.erpsystem.model.CategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryModel, Long> {

    CategoryModel findByCategoryName(String categoryName);

}
