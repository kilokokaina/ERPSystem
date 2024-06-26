package com.work.erpsystem.repository;

import com.work.erpsystem.model.OrganizationModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<OrganizationModel, Long> {

    OrganizationModel findByOrgName(String orgName);
    OrganizationModel findByOrgUUID(String orgUUID);

}
