package com.work.erpsystem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class ItemModel {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long itemId;

    private String itemName;

    private Date itemCreationDate = new Date();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryModel categoryModel;

    private Double itemPurchasePrice;
    private Double itemSalePrice;

}
