package com.work.erpsystem.repository;

import com.work.erpsystem.model.WarehouseModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<WarehouseModel, Long> {
}
