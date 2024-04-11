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
    public WarehouseModel save(WarehouseModel warehouseModel) throws DuplicateDBRecord {
        if (warehouseRepository.findByWarehouseName(warehouseModel.getWarehouseName()) != null) {
            String exceptionMessage = "Record with name [%s] already exists in DB";
            throw new DuplicateDBRecord(exceptionMessage);
        }

        return warehouseRepository.save(warehouseModel);
    }

    @Override
    public WarehouseModel update(WarehouseModel warehouseModel) {
        return warehouseRepository.save(warehouseModel);
    }

    @Override
    public List<WarehouseModel> findAll() {
        return warehouseRepository.findAll();
    }

    @Override
    public WarehouseModel findById(Long warehouseModelId) throws NoDBRecord {
        WarehouseModel warehouseModel = warehouseRepository.findById(warehouseModelId).orElse(null);

        if (Objects.isNull(warehouseModel)) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, warehouseModelId));
        }

        return warehouseModel;
    }

    @Override
    public WarehouseModel findByName(String warehouseModelName) throws NoDBRecord {
        WarehouseModel warehouseModel = warehouseRepository.findByWarehouseName(warehouseModelName);

        if (Objects.isNull(warehouseModel)) {
            String exceptionMessage = "No such record in data base with name: %s";
            throw new NoDBRecord(String.format(exceptionMessage, warehouseModelName));
        }

        return warehouseModel;
    }

    @Override
    public List<WarehouseModel> findByOrganization(OrganizationModel organizationModel) {
        return warehouseRepository.findByOrganization(organizationModel);
    }

    @Override
    public void deleteById(Long warehouseModelId) throws NoDBRecord {
        WarehouseModel warehouseModel = this.findById(warehouseModelId);

        if (Objects.isNull(warehouseModel)) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, warehouseModelId));
        }

        warehouseRepository.deleteById(warehouseModelId);
    }

    @Override
    public void delete(WarehouseModel warehouseModel) throws NoDBRecord {
        warehouseRepository.delete(warehouseModel);
    }
}
