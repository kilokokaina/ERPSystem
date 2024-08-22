package com.work.erpsystem.service.impl;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.BarcodeModel;
import com.work.erpsystem.model.ItemModel;
import com.work.erpsystem.model.OrganizationModel;
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
    public ItemModel save(ItemModel item) throws DuplicateDBRecord {
//        if (itemRepository.findByItemName(itemModel.getItemName()) != null) {
//            String exceptionMessage = "Record with name [%s] already exists in DB";
//            throw new DuplicateDBRecord(exceptionMessage);
//        }

        return itemRepository.save(item);
    }

    @Override
    public ItemModel update(ItemModel item) throws NoDBRecord {
        if (Objects.isNull(itemRepository.findById(item.getItemId()).orElse(null))) {
            throw new NoDBRecord(String.format("No such record in data base with id: %d", item.getItemId()));
        }

        return itemRepository.save(item);
    }

    @Override
    public List<ItemModel> findAll() {
        return itemRepository.findAll();
    }

    @Override
    public ItemModel findById(Long itemId) throws NoDBRecord {
        ItemModel item = itemRepository.findById(itemId).orElse(null);

        if (Objects.isNull(item)) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, itemId));
        }

        return item;
    }

    @Override
    public ItemModel findByName(String itemName) throws NoDBRecord {
        ItemModel item = itemRepository.findByItemName(itemName);

        if (Objects.isNull(item)) {
            String exceptionMessage = "No such record in data base with name: %s";
            throw new NoDBRecord(String.format(exceptionMessage, itemName));
        }

        return item;
    }

    @Override
    public List<ItemModel> findByCategoryId(Long categoryId) {
        return itemRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<ItemModel> findByOrganizationModel(OrganizationModel organization) {
        return itemRepository.findByOrganizationModel(organization);
    }

    @Override
    public ItemModel findByBarcode(BarcodeModel barcode) throws NoDBRecord {
        ItemModel item = itemRepository.findByBarcode(barcode);

        if (Objects.isNull(item)) {
            String exceptionMessage = "No such record in data base with barcode: %s";
            throw new NoDBRecord(String.format(exceptionMessage, barcode.getCodeValue()));
        }

        return item;
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
    public void delete(ItemModel item) {
        itemRepository.delete(item);
    }
}
