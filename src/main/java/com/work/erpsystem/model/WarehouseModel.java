package com.work.erpsystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Entity
@NoArgsConstructor
public class WarehouseModel {

    @Id @GeneratedValue
    private Long warehouseId;

    private String warehouseName;
    private String warehouseAddress;

    @ManyToOne
    @JoinColumn(name = "org_id")
    private OrganizationModel organization;

    @ElementCollection
    @CollectionTable(name = "warehouse_item_storage",
            joinColumns = { @JoinColumn(name = "warehouse_id") })
    @MapKeyJoinColumn(name = "item_id")
    private Map<ItemModel, Integer> itemQuantity;

//    @ElementCollection
//    @CollectionTable(name = "warehouse_item_sales",
//            joinColumns = { @JoinColumn(name = "warehouse_id") })
//    @MapKeyJoinColumn(name = "item_id")
//    private Map<ItemModel, Integer> itemSales;

    @ElementCollection
    @CollectionTable(name = "warehouse_item_price",
            joinColumns = { @JoinColumn(name = "warehouse_id") })
    @MapKeyJoinColumn(name = "item_id")
    private Map<ItemModel, Double> itemPrice;

}
