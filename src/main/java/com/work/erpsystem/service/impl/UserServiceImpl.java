package com.work.erpsystem.service.impl;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.OrganizationModel;
import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.repository.UserRepository;
import com.work.erpsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserModel save(UserModel userModel) throws DuplicateDBRecord {
        if (Objects.nonNull(userRepository.findByUsername(userModel.getUsername()))) {
            String exceptionMessage = "Username with name [%s] already exist in DB";
            throw new DuplicateDBRecord(String.format(exceptionMessage, userModel.getUsername()));
        }

        return userRepository.save(userModel);
    }

    @Override
    public UserModel update(UserModel userModel) throws NoDBRecord {
        if (Objects.isNull(this.findById(userModel.getUserId()))) {
            throw new NoDBRecord(String.format("No user with name: %s", userModel.getUsername()));
        }

        return userRepository.save(userModel);
    }

    @Override
    public List<UserModel> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserModel findById(Long userId) throws NoDBRecord {
        UserModel userModel = userRepository.findById(userId).orElse(null);

        if (Objects.isNull(userModel)) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, userId));
        }

        return userModel;
    }

    @Override
    public UserModel findByUsername(String username) throws UsernameNotFoundException {
        UserModel userModel = userRepository.findByUsername(username);

        if (Objects.isNull(userModel)) {
            String exceptionMessage = "No such user with name: %s";
            throw new UsernameNotFoundException(String.format(exceptionMessage, username));
        }

        return userModel;
    }

    @Override
    public UserModel findByUUID(String UUID) throws NoDBRecord {
        UserModel userModel = userRepository.findByUserUUID(UUID);

        if (Objects.isNull(userModel)) {
            String exceptionMessage = "No such record in data base with UUID: %s";
            throw new NoDBRecord(String.format(exceptionMessage, UUID));
        }

        return userModel;
    }

    @Override
    public List<UserModel> findByEmployeeOrg(OrganizationModel organizationModel) {
        return userRepository.findAll().stream().filter(user -> user.getOrgRole().containsKey(organizationModel)).toList();
    }

    @Override
    public void deleteById(Long userId) throws NoDBRecord {
        if (Objects.isNull(userRepository.findById(userId).orElse(null))) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, userId));
        }

        userRepository.deleteById(userId);
    }

    @Override
    public void delete(UserModel userModel) throws NoDBRecord {
        userRepository.delete(userModel);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.findByUsername(username);
    }
}
