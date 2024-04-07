package com.work.erpsystem.repository;

import com.work.erpsystem.model.ItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<ItemModel, Long> {

    ItemModel findByItemName(String itemModelName);

    @Query(nativeQuery = true, value = "SELECT * FROM item_model WHERE category_id = %:categoryId%")
    List<ItemModel> findByCategoryId(Long categoryId);

}
