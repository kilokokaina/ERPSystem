package com.work.erpsystem.service;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.ItemModel;

import java.util.List;

public interface ItemService {

    ItemModel save(ItemModel itemModel) throws DuplicateDBRecord;
    ItemModel update(ItemModel itemModel) throws NoDBRecord;
    List<ItemModel> findAll();
    ItemModel findById(Long itemId) throws NoDBRecord;
    ItemModel findByName(String itemName) throws NoDBRecord;
    List<ItemModel> findByCategoryId(Long categoryId);
    void deleteById(Long itemId) throws NoDBRecord;
    void delete(ItemModel itemModel) throws NoDBRecord;

}
