package com.work.erpsystem.service;

import com.work.erpsystem.model.UserModel;

import java.util.List;

public interface UserService {

    UserModel save(UserModel userModel);
    List<UserModel> findAll();
    UserModel findById(Long userId);
    UserModel findByUsername(String username);
    void deleteById(Long userId);
    void delete(UserModel userModel);

}
