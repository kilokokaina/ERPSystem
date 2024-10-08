package com.work.erpsystem.service.impl;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.OrganizationModel;
import com.work.erpsystem.model.WarehouseModel;
import com.work.erpsystem.repository.WarehouseRepository;
import com.work.erpsystem.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Autowired
    public WarehouseServiceImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public WarehouseModel save(WarehouseModel warehouse) throws DuplicateDBRecord {
//        if (warehouseRepository.findByWarehouseName(warehouseModel.getWarehouseName()) != null) {
//            String exceptionMessage = "Record with name [%s] already exists in DB";
//            throw new DuplicateDBRecord(exceptionMessage);
//        }

        return warehouseRepository.save(warehouse);
    }

    @Override
    public WarehouseModel update(WarehouseModel warehouse) throws NoDBRecord {
        if (Objects.isNull(warehouseRepository.findById(warehouse.getWarehouseId()).orElse(null))) {
            throw new NoDBRecord(String.format("No such record in data base with id: %d", warehouse.getWarehouseId()));
        }

        return warehouseRepository.save(warehouse);
    }

    @Override
    public List<WarehouseModel> findAll() {
        return warehouseRepository.findAll();
    }

    @Override
    public WarehouseModel findById(Long warehouseId) throws NoDBRecord {
        WarehouseModel warehouse = warehouseRepository.findById(warehouseId).orElse(null);

        if (Objects.isNull(warehouse)) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, warehouseId));
        }

        return warehouse;
    }

    @Override
    public WarehouseModel findByName(String warehouseName) throws NoDBRecord {
        WarehouseModel warehouse = warehouseRepository.findByWarehouseName(warehouseName);

        if (Objects.isNull(warehouse)) {
            String exceptionMessage = "No such record in data base with name: %s";
            throw new NoDBRecord(String.format(exceptionMessage, warehouseName));
        }

        return warehouse;
    }

    @Override
    public List<WarehouseModel> findByOrganization(OrganizationModel organization) {
        return warehouseRepository.findByOrganization(organization);
    }

    @Override
    public void deleteById(Long warehouseId) throws NoDBRecord {
        if (Objects.isNull(warehouseRepository.findById(warehouseId).orElse(null))) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, warehouseId));
        }

        warehouseRepository.deleteById(warehouseId);
    }

    @Override
    public void delete(WarehouseModel warehouse) throws NoDBRecord {
        warehouseRepository.delete(warehouse);
    }
}
