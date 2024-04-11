package com.work.erpsystem.service.impl;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.ItemModel;
import com.work.erpsystem.repository.ItemRepository;
import com.work.erpsystem.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemModel save(ItemModel itemModel) throws DuplicateDBRecord {
        if (itemRepository.findByItemName(itemModel.getItemName()) != null) {
            String exceptionMessage = "Record with name [%s] already exists in DB";
            throw new DuplicateDBRecord(exceptionMessage);
        }

        return itemRepository.save(itemModel);
    }

    @Override
    public ItemModel update(ItemModel itemModel) {
        return itemRepository.save(itemModel);
    }

    @Override
    public List<ItemModel> findAll() {
        return itemRepository.findAll();
    }

    @Override
    public ItemModel findById(Long itemModelId) throws NoDBRecord {
        ItemModel itemModel = itemRepository.findById(itemModelId).orElse(null);

        if (Objects.isNull(itemModel)) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, itemModelId));
        }

        return itemModel;
    }

    @Override
    public ItemModel findByName(String itemModelName) throws NoDBRecord {
        ItemModel itemModel = itemRepository.findByItemName(itemModelName);

        if (Objects.isNull(itemModel)) {
            String exceptionMessage = "No such record in data base with name: %s";
            throw new NoDBRecord(String.format(exceptionMessage, itemModelName));
        }

        return itemModel;
    }

    @Override
    public List<ItemModel> findByCategoryId(Long categoryId) {
        return itemRepository.findByCategoryId(categoryId);
    }

    @Override
    public void deleteById(Long itemModelId) throws NoDBRecord {
        ItemModel itemModel = this.findById(itemModelId);

        if (Objects.isNull(itemModel)) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, itemModelId));
        }

        itemRepository.deleteById(itemModelId);

    }

    @Override
    public void delete(ItemModel itemModel) {
        itemRepository.delete(itemModel);
    }
}
