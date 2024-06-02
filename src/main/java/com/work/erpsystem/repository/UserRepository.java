package com.work.erpsystem.repository;

import com.work.erpsystem.model.OrganizationModel;
import com.work.erpsystem.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    UserModel findByUsername(String username);
    List<UserModel> findByOrgEmployee(OrganizationModel organizationModel);

}
