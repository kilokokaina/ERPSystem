package com.work.erpsystem.service.impl.auth;

import com.work.erpsystem.exception.DuplicateDBRecord;
import com.work.erpsystem.model.Role;
import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.service.UserAuthenticationService;
import com.work.erpsystem.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class UserAuthenticationImpl implements UserAuthenticationService {

    private final UserServiceImpl userService;

    @Autowired
    public UserAuthenticationImpl(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public boolean register(String username, String password, String firstName, String secondName) {
        try {
            UserModel user = new UserModel();

            user.setUsername(username);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setSecondName(secondName);
            user.setRoleSet(Set.of(Role.USER));

            userService.save(user);
        } catch (DuplicateDBRecord exception) {
            return false;
        }

        return true;
    }
}
