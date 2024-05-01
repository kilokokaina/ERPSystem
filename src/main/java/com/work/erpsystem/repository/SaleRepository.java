package com.work.erpsystem.repository;

import com.work.erpsystem.model.SaleModel;
import com.work.erpsystem.model.WarehouseModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleRepository extends JpaRepository<SaleModel, Long> {
    List<SaleModel> findByWarehouse(WarehouseModel warehouse);
}
