package com.work.erpsystem.service;

import com.work.erpsystem.model.ItemModel;

import java.util.List;

public interface ItemService {

    ItemModel save(ItemModel itemModel);
    ItemModel findById(Long itemModelId);
    ItemModel findByName(String itemModelName);
    List<ItemModel> findByCategoryId(Long categoryId);
    List<ItemModel> findAll();
    ItemModel deleteById(Long itemModelId);
    ItemModel delete(ItemModel itemModel);

}
