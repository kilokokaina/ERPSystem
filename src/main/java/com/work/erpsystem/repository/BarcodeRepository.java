package com.work.erpsystem.repository;

import com.work.erpsystem.model.BarcodeModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarcodeRepository extends JpaRepository<BarcodeModel, Long> {
}
