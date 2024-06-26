package com.work.erpsystem.service;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.OrganizationModel;

import java.util.List;

public interface OrgService {

    OrganizationModel save(OrganizationModel organizationModel) throws DuplicateDBRecord;
    OrganizationModel update(OrganizationModel organizationModel) throws NoDBRecord;
    List<OrganizationModel> findAll();
    OrganizationModel findById(Long organizationId) throws NoDBRecord;
    OrganizationModel findByName(String organizationName) throws NoDBRecord;
    OrganizationModel findByUUID(String organizationUUID) throws NoDBRecord;
    void deleteById(Long organizationId) throws NoDBRecord;
    void delete(OrganizationModel organizationModel) throws NoDBRecord;

}
