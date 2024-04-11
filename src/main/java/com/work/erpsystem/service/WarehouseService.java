package com.work.erpsystem.service;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.OrganizationModel;
import com.work.erpsystem.model.WarehouseModel;

import java.util.List;

public interface WarehouseService {

    WarehouseModel save(WarehouseModel warehouseModel) throws DuplicateDBRecord;
    WarehouseModel update(WarehouseModel warehouseModel);
    List<WarehouseModel> findAll();
    WarehouseModel findById(Long warehouseModelId) throws NoDBRecord;
    WarehouseModel findByName(String warehouseModelName) throws NoDBRecord;
    List<WarehouseModel> findByOrganization(OrganizationModel organizationModel);
    void deleteById(Long warehouseModelId) throws NoDBRecord;
    void delete(WarehouseModel warehouseModel) throws NoDBRecord;

}
