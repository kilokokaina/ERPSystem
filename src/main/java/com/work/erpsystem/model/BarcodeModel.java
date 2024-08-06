package com.work.erpsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class BarcodeModel {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long codeId;

    private String codeValue;

    private Date scanDate = new Date();

}
