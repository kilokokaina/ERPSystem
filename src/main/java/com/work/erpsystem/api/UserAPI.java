package com.work.erpsystem.api;

import com.work.erpsystem.dto.UserDTO;
import com.work.erpsystem.exception.DBException;
import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.OrganizationModel;
import com.work.erpsystem.model.Role;
import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.service.impl.OrgServiceImpl;
import com.work.erpsystem.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
public class UserAPI {

    private final UserServiceImpl userService;
    private final OrgServiceImpl orgService;
    private final JavaMailSender mailSender;

    @Autowired
    public UserAPI(UserServiceImpl userService, JavaMailSender mailSender, OrgServiceImpl orgService) {
        this.userService = userService;
        this.orgService = orgService;
        this.mailSender = mailSender;
    }

    private static SimpleMailMessage sendMessageToNewUser(UserModel newUser) {
        SimpleMailMessage message = new SimpleMailMessage();
        String messageText = String.format("""
            К вашей почте был привязан аккаунт в ERPSystem. Перейдите по ссылке для того, чтобы задать пароль
            %s
        """, newUser.getUserUUID());

        message.setFrom("nikolaushki@yandex.ru");
        message.setSubject("Вас пригласили в организацию - ERPSystem");
        message.setTo(newUser.getUsername());
        message.setText(messageText);

        return message;
    }

    @GetMapping("/api/user")
    public @ResponseBody ResponseEntity<UserModel> findUser(@RequestParam(value = "email") String email) {
        try {
            return ResponseEntity.ok(userService.findByUsername(email));
        } catch (UsernameNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
    
    @PostMapping("{org_uuid}/api/user")
    public @ResponseBody ResponseEntity<UserModel> createUser(@PathVariable(value = "org_uuid") Long orgId,
                                                              @RequestBody UserDTO userDTO, Authentication authentication) {
        try {
            OrganizationModel organizationModel = orgService.findById(orgId);
            Map<OrganizationModel, String> orgRole = new HashMap<>();
            orgRole.put(organizationModel, Role.valueOf(userDTO.getUserAuthority()).name());

            UserModel newUser = new UserModel();

            newUser.setUsername(userDTO.getEmail());
            newUser.setFirstName(userDTO.getFirstName());
            newUser.setSecondName(userDTO.getSecondName());
            newUser.setPost(userDTO.getPost());
            newUser.setOrgRole(orgRole);
            newUser.setRoleSet(Set.of(Role.USER));

            userService.save(newUser);
            SimpleMailMessage message = sendMessageToNewUser(newUser);
            mailSender.send(message);

            return ResponseEntity.ok(newUser);
        } catch (DBException exception) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("{org_uuid}/api/user/invite")
    public @ResponseBody ResponseEntity<HttpStatus> inviteUser(@PathVariable(value = "org_uuid") Long orgId,
                                                               @RequestParam(name = "user_id") Long userId,
                                                               @RequestParam(name = "role") String role,
                                                               Authentication authentication) {
        try {
            UserModel user = userService.findById(userId);
            Map<OrganizationModel, String> orgRole = user.getOrgRole();

            orgRole.put(orgService.findById(orgId), Role.valueOf(role).name());
            user.setOrgRole(orgRole);
            userService.update(user);

            return ResponseEntity.ok(HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

}
