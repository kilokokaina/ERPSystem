package com.work.erpsystem.repository;

import com.work.erpsystem.model.BarcodeModel;
import com.work.erpsystem.model.ItemModel;
import com.work.erpsystem.model.OrganizationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<ItemModel, Long> {

    ItemModel findByItemName(String itemModelName);
    ItemModel findByBarcode(BarcodeModel barcode);
    List<ItemModel> findByOrganizationModel(OrganizationModel org);
    @Query(nativeQuery = true, value = "SELECT * FROM item_model WHERE category_id = %:categoryId%")
    List<ItemModel> findByCategoryId(Long categoryId);

}
