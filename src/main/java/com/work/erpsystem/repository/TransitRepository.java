package com.work.erpsystem.repository;

import com.work.erpsystem.model.OrganizationModel;
import com.work.erpsystem.model.TransitModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransitRepository extends JpaRepository<TransitModel, Long> {

    List<TransitModel> findByOrganization(OrganizationModel organization);

}
