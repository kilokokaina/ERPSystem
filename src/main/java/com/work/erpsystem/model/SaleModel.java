package com.work.erpsystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class SaleModel {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long saleId;

    private Integer itemSaleQuantity;
    private Double itemSalePrice;

    private Date saleDate = new Date();

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private WarehouseModel warehouse;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemModel item;

}
