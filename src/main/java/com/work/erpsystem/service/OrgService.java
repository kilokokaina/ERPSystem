package com.work.erpsystem.service;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.OrganizationModel;

import java.util.List;

public interface OrgService {

    OrganizationModel save(OrganizationModel organizationModel) throws DuplicateDBRecord;
    OrganizationModel update(OrganizationModel organizationModel);
    List<OrganizationModel> findAll();
    OrganizationModel findById(Long organizationModelId) throws NoDBRecord;
    OrganizationModel findByName(String organizationModelName) throws NoDBRecord;
    void deleteById(Long organizationModelId) throws NoDBRecord;
    void delete(OrganizationModel organizationModel) throws NoDBRecord;

}
