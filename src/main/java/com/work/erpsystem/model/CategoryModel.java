package com.work.erpsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class CategoryModel {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long categoryId;

    private String categoryName;

}
