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
            OrganizationModel organizationModel = orgService.findById(organizationId);

            return new ResponseEntity<>(organizationModel, HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("find_by_name")
    public @ResponseBody ResponseEntity<OrganizationModel> findByName(@PathVariable(value = "org_uuid") Long orgId,
                                                                      @RequestParam(value = "orgName") String orgName,
                                                                      Authentication authentication) {
        try {
            OrganizationModel organizationModel = orgService.findByName(orgName);

            return new ResponseEntity<>(organizationModel, HttpStatus.OK);
        } catch (NoDBRecord exception) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping
    public @ResponseBody ResponseEntity<OrganizationModel> addOrg(@PathVariable(value = "org_uuid", required = false) Long orgId,
                                                                  @RequestBody OrgDTO orgDTO, Authentication authentication) {
        UserModel userModel = userService.findByUsername(authentication.getName());
        OrganizationModel organizationModel = new OrganizationModel();
        try {
            organizationModel.setOrgName(orgDTO.getOrgName());
            organizationModel.setOrgAddress(orgDTO.getOrgAddress());

            orgService.save(organizationModel);

            Map<OrganizationModel, String> orgRole = userModel.getOrgRole();
            orgRole.put(organizationModel, Role.OWNER.name());
            userModel.setOrgRole(orgRole);

            userService.update(userModel);

            return ResponseEntity.ok(organizationModel);
        } catch (DBException exception) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("{id}")
    public @ResponseBody ResponseEntity<OrganizationModel> updateOrg(@PathVariable(value = "org_uuid") Long orgId,
                                                                     @PathVariable(value = "id") Long organizationId,
                                                                     @RequestBody OrgDTO orgDTO, Authentication authentication) {
        try {
            OrganizationModel organizationModel = orgService.findById(organizationId);

            organizationModel.setOrgName(orgDTO.getOrgName());
            organizationModel.setOrgAddress(orgDTO.getOrgAddress());

            return ResponseEntity.ok(orgService.update(organizationModel));
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
