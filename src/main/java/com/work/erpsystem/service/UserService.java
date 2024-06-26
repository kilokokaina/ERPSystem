package com.work.erpsystem.service;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.OrganizationModel;
import com.work.erpsystem.model.UserModel;

import java.util.List;

public interface UserService {

    UserModel save(UserModel userModel) throws DuplicateDBRecord;
    UserModel update(UserModel userModel) throws NoDBRecord;
    List<UserModel> findAll();
    UserModel findById(Long userId) throws NoDBRecord;
    UserModel findByUsername(String username) throws NoDBRecord;
    List<UserModel> findByEmployeeOrg(OrganizationModel organizationModel);
    UserModel findByUUID(String username) throws NoDBRecord;
    void deleteById(Long userId) throws NoDBRecord;
    void delete(UserModel userModel) throws NoDBRecord;

}
