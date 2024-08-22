package com.work.erpsystem.service;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.OrganizationModel;
import com.work.erpsystem.model.WarehouseModel;

import java.util.List;

public interface WarehouseService {

    WarehouseModel save(WarehouseModel warehouse) throws DuplicateDBRecord;
    WarehouseModel update(WarehouseModel warehouse) throws NoDBRecord;
    List<WarehouseModel> findAll();
    WarehouseModel findById(Long warehouseId) throws NoDBRecord;
    WarehouseModel findByName(String warehouseName) throws NoDBRecord;
    List<WarehouseModel> findByOrganization(OrganizationModel organization);
    void deleteById(Long warehouseId) throws NoDBRecord;
    void delete(WarehouseModel warehouse) throws NoDBRecord;

}
