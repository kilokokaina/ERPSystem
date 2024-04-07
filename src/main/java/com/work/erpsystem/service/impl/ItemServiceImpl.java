package com.work.erpsystem.service.impl;

import com.work.erpsystem.model.ItemModel;
import com.work.erpsystem.repository.ItemRepository;
import com.work.erpsystem.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemModel save(ItemModel itemModel) {
        return itemRepository.save(itemModel);
    }

    @Override
    public ItemModel findById(Long itemModelId) {
        return itemRepository.findById(itemModelId).orElse(null);
    }

    @Override
    public ItemModel findByName(String itemModelName) {
        return itemRepository.findByItemName(itemModelName);
    }

    @Override
    public List<ItemModel> findByCategoryId(Long categoryId) {
        return itemRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<ItemModel> findAll() {
        return itemRepository.findAll();
    }

    @Override
    public ItemModel deleteById(Long itemModelId) {
        ItemModel itemModel = this.findById(itemModelId);
        itemRepository.deleteById(itemModelId);

        return itemModel;
    }

    @Override
    public ItemModel delete(ItemModel itemModel) {
        itemRepository.delete(itemModel);
        return itemModel;
    }
}
