package com.work.erpsystem.api;

import com.work.erpsystem.dto.OrgDTO;
import com.work.erpsystem.exception.DBException;
import com.work.erpsystem.exception.DuplicateDBRecord;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("{org_uuid}/api/org")
public class OrganizationAPI {

    private final OrgServiceImpl orgService;
    private final UserServiceImpl userService;

    @Autowired
    public OrganizationAPI(OrgServiceImpl orgService, UserServiceImpl userService) {
        this.userService = userService;
        this.orgService = orgService;
    }

    @GetMapping("{id}")
    public @ResponseBody ResponseEntity<OrganizationModel> findById(@PathVariable(value = "org_uuid") Long orgId,
                                                                    @PathVariable(value = "id") Long organizationId,
                                                                    Authentication authentication) {
        try {
            OrganizationModel organization = orgService.findById(organizationId);

            return new ResponseEntity<>(organization, HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("find_by_name")
    public @ResponseBody ResponseEntity<OrganizationModel> findByName(@PathVariable(value = "org_uuid") Long orgId,
                                                                      @RequestParam(value = "orgName") String orgName,
                                                                      Authentication authentication) {
        try {
            OrganizationModel organization = orgService.findByName(orgName);

            return new ResponseEntity<>(organization, HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("add_cp/{id}")
    public @ResponseBody ResponseEntity<UserModel> addContactPerson(@PathVariable(value = "org_uuid") Long orgId,
                                                                    @PathVariable(value = "id") Long userId,
                                                                    Authentication authentication) {
        try {
            UserModel user = userService.findById(userId);
            OrganizationModel organization = orgService.findById(orgId);

            organization.setContactPerson(user.getUserId());
            orgService.update(organization);

            return ResponseEntity.ok(user);
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping
    public @ResponseBody ResponseEntity<OrganizationModel> addOrg(@PathVariable(value = "org_uuid", required = false) Long orgId,
                                                                  @RequestBody OrgDTO orgDTO, Authentication authentication) {
        UserModel user = userService.findByUsername(authentication.getName());
        OrganizationModel organization = new OrganizationModel();
        try {
            organization.setOrgName(orgDTO.getOrgName());
            organization.setOrgAddress(orgDTO.getOrgAddress());

            orgService.save(organization);

            Map<OrganizationModel, String> orgRole = user.getOrgRole();
            orgRole.put(organization, Role.OWNER.name());
            user.setOrgRole(orgRole);

            userService.update(user);

            return ResponseEntity.ok(organization);
        } catch (DBException exception) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("{id}")
    public @ResponseBody ResponseEntity<OrganizationModel> updateOrg(@PathVariable(value = "org_uuid") Long orgId,
                                                                     @PathVariable(value = "id") Long updateOrgId,
                                                                     @RequestBody OrgDTO orgDTO, Authentication authentication) {
        try {
            OrganizationModel organization = orgService.findById(updateOrgId);

            organization.setOrgName(orgDTO.getOrgName());
            organization.setOrgAddress(orgDTO.getOrgAddress());

            return ResponseEntity.ok(orgService.update(organization));
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping("{id}")
    public @ResponseBody ResponseEntity<HttpStatus> deleteById(@PathVariable(value = "org_uuid") Long orgId,
                                                               @PathVariable(value = "id") Long organizationId,
                                                               Authentication authentication) {
        try {
            orgService.deleteById(organizationId);

            return ResponseEntity.ok(HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return ResponseEntity.ok(HttpStatus.NO_CONTENT);
        }
    }

}
