package com.work.erpsystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class OrganizationModel {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orgId;

    private String orgName;

}
