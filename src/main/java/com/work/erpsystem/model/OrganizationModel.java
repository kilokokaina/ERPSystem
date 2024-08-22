package com.work.erpsystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
public class OrganizationModel {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orgId;

    @Column(nullable = false)
    private String orgName;
    private String orgAddress;

    private Date creationDate = new Date();

    private String orgUUID = UUID.randomUUID().toString();

    private Long contactPerson;

}
