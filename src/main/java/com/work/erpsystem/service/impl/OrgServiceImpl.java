package com.work.erpsystem.service.impl;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.OrganizationModel;
import com.work.erpsystem.repository.OrganizationRepository;
import com.work.erpsystem.service.OrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class OrgServiceImpl implements OrgService {

    private final OrganizationRepository organizationRepository;

    @Autowired
    public OrgServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }


    @Override
    public OrganizationModel save(OrganizationModel organizationModel) throws DuplicateDBRecord {
        if (organizationRepository.findByOrgName(organizationModel.getOrgName()) != null) {
            String exceptionMessage = "Record with name [%s] already exists in DB";
            throw new DuplicateDBRecord(exceptionMessage);
        }

        return organizationRepository.save(organizationModel);
    }

    @Override
    public OrganizationModel update(OrganizationModel organizationModel) throws NoDBRecord {
        if (Objects.isNull(organizationRepository.findById(organizationModel.getOrgId()).orElse(null))) {
            throw new NoDBRecord(String.format("No such record in data base with id: %d", organizationModel.getOrgId()));
        }

        return organizationRepository.save(organizationModel);
    }

    @Override
    public List<OrganizationModel> findAll() {
        return organizationRepository.findAll();
    }

    @Override
    public OrganizationModel findById(Long organizationId) throws NoDBRecord {
        OrganizationModel organizationModel = organizationRepository.findById(organizationId).orElse(null);

        if (Objects.isNull(organizationModel)) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, organizationId));
        }

        return organizationModel;
    }

    @Override
    public OrganizationModel findByName(String organizationName) throws NoDBRecord {
        OrganizationModel organizationModel = organizationRepository.findByOrgName(organizationName);

        if (Objects.isNull(organizationModel)) {
            String exceptionMessage = "No such record in data base with name: %d";
            throw new NoDBRecord(String.format(exceptionMessage, organizationName));
        }

        return organizationModel;
    }

    @Override
    public OrganizationModel findByUUID(String organizationUUID) throws NoDBRecord {
        OrganizationModel organizationModel = organizationRepository.findByOrgUUID(organizationUUID);

        if (Objects.isNull(organizationModel)) {
            String exceptionMessage = "No such record in data base with name: %d";
            throw new NoDBRecord(String.format(exceptionMessage, organizationUUID));
        }

        return organizationModel;
    }

    @Override
    public void deleteById(Long organizationId) throws NoDBRecord {
        if (Objects.isNull(organizationRepository.findById(organizationId).orElse(null))) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, organizationId));
        }

        organizationRepository.deleteById(organizationId);
    }

    @Override
    public void delete(OrganizationModel organizationModel) throws NoDBRecord {
        organizationRepository.delete(organizationModel);
    }
}
