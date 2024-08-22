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
    public UserModel save(UserModel user) throws DuplicateDBRecord {
        if (Objects.nonNull(userRepository.findByUsername(user.getUsername()))) {
            String exceptionMessage = "Username with name [%s] already exist in DB";
            throw new DuplicateDBRecord(String.format(exceptionMessage, user.getUsername()));
        }

        return userRepository.save(user);
    }

    @Override
    public UserModel update(UserModel user) throws NoDBRecord {
        if (Objects.isNull(this.findById(user.getUserId()))) {
            throw new NoDBRecord(String.format("No user with name: %s", user.getUsername()));
        }

        return userRepository.save(user);
    }

    @Override
    public List<UserModel> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserModel findById(Long userId) throws NoDBRecord {
        UserModel user = userRepository.findById(userId).orElse(null);

        if (Objects.isNull(user)) {
            String exceptionMessage = "No such record in data base with id: %d";
            throw new NoDBRecord(String.format(exceptionMessage, userId));
        }

        return user;
    }

    @Override
    public UserModel findByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userRepository.findByUsername(username);

        if (Objects.isNull(user)) {
            String exceptionMessage = "No such user with name: %s";
            throw new UsernameNotFoundException(String.format(exceptionMessage, username));
        }

        return user;
    }

    @Override
    public UserModel findByUUID(String UUID) throws NoDBRecord {
        UserModel user = userRepository.findByUserUUID(UUID);

        if (Objects.isNull(user)) {
            String exceptionMessage = "No such record in data base with UUID: %s";
            throw new NoDBRecord(String.format(exceptionMessage, UUID));
        }

        return user;
    }

    @Override
    public List<UserModel> findByEmployeeOrg(OrganizationModel organization) {
        return userRepository.findAll().stream().filter(user -> user.getOrgRole().containsKey(organization)).toList();
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
    public void delete(UserModel user) throws NoDBRecord {
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.findByUsername(username);
    }
}
