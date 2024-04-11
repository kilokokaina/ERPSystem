package com.work.erpsystem.repository;

import com.work.erpsystem.model.OrganizationModel;
import com.work.erpsystem.model.WarehouseModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<WarehouseModel, Long> {

    List<WarehouseModel> findByOrganization(OrganizationModel organization);
    WarehouseModel findByWarehouseName(String warehouseName);

}
