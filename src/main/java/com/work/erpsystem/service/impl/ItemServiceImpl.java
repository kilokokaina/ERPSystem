package com.work.erpsystem.service.impl;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.BarcodeModel;
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
//        if (itemRepository.findByItemName(itemModel.getItemName()) != null) {
//            String exceptionMessage = "Record with name [%s] already exists in DB";
//            throw new DuplicateDBRecord(exceptionMessage);
//        }

        return itemRepository.save(itemModel);
    }

    @Override
    public ItemModel update(ItemModel itemModel) throws NoDBRecord {
        if (Objects.isNull(itemRepository.findById(itemModel.getItemId()).orElse(null))) {
            throw new NoDBRecord(String.format("No such record in data base with id: %d", itemModel.getItemId()));
        }

        return itemRepository.save(itemModel);
    }

    @Override
    public List<ItemModel> findAll() {
        return itemRepository.findAll();
    }

    @Override
    public ItemModel findById(Long itemId) throws NoDBRecord {
        ItemModel itemModel = itemRepository.findById(itemId).orElse(null);

        if (Objects.isNull(itemModel)) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, itemId));
        }

        return itemModel;
    }

    @Override
    public ItemModel findByName(String itemName) throws NoDBRecord {
        ItemModel itemModel = itemRepository.findByItemName(itemName);

        if (Objects.isNull(itemModel)) {
            String exceptionMessage = "No such record in data base with name: %s";
            throw new NoDBRecord(String.format(exceptionMessage, itemName));
        }

        return itemModel;
    }

    @Override
    public List<ItemModel> findByCategoryId(Long categoryId) {
        return itemRepository.findByCategoryId(categoryId);
    }

    public ItemModel findByBarcode(BarcodeModel barcode) throws NoDBRecord {
        ItemModel itemModel = itemRepository.findByBarcode(barcode);

        if (Objects.isNull(itemModel)) {
            String exceptionMessage = "No such record in data base with barcode: %s";
            throw new NoDBRecord(String.format(exceptionMessage, barcode.getCodeValue()));
        }

        return itemModel;
    }

    @Override
    public void deleteById(Long itemId) throws NoDBRecord {
        if (Objects.isNull(itemRepository.findById(itemId).orElse(null))) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, itemId));
        }

        itemRepository.deleteById(itemId);
    }

    @Override
    public void delete(ItemModel itemModel) {
        itemRepository.delete(itemModel);
    }
}
