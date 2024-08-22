package com.work.erpsystem.service;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.BarcodeModel;
import com.work.erpsystem.model.ItemModel;
import com.work.erpsystem.model.OrganizationModel;

import java.util.List;

public interface ItemService {

    ItemModel save(ItemModel item) throws DuplicateDBRecord;
    ItemModel update(ItemModel item) throws NoDBRecord;
    List<ItemModel> findAll();
    ItemModel findById(Long itemId) throws NoDBRecord;
    ItemModel findByName(String itemName) throws NoDBRecord;
    List<ItemModel> findByCategoryId(Long categoryId);
    List<ItemModel> findByOrganizationModel(OrganizationModel organization);
    ItemModel findByBarcode(BarcodeModel barcode) throws NoDBRecord;
    void deleteById(Long itemId) throws NoDBRecord;
    void delete(ItemModel item) throws NoDBRecord;

}
