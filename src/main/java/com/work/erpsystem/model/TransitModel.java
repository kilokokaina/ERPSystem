package com.work.erpsystem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@Entity
public class TransitModel {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long transitId;

    private String transitStatus;

    @ElementCollection
    @CollectionTable(name = "transit_item_quantity", joinColumns = @JoinColumn(name = "transit_id"))
    @MapKeyJoinColumn(name = "item_id")
    private Map<ItemModel, Integer> itemQuantity;

    @ElementCollection
    @CollectionTable(name = "transit_item_price", joinColumns = @JoinColumn(name = "transit_id"))
    @MapKeyJoinColumn(name = "item_id")
    private Map<ItemModel, Double> itemPrice;

    @ManyToOne
    @JoinTable(
            name = "depart_point", joinColumns = @JoinColumn(name = "transit_id"),
            inverseJoinColumns = @JoinColumn(name = "warehouse_id")
    )
    private WarehouseModel departPoint;

    @ManyToOne
    @JoinTable(
            name = "arrive_point", joinColumns = @JoinColumn(name = "transit_id"),
            inverseJoinColumns = @JoinColumn(name = "warehouse_id")
    )
    private WarehouseModel arrivePoint;

    @ManyToOne
    @JoinTable(
            name = "transit_org", joinColumns = @JoinColumn(name = "transit_id"),
            inverseJoinColumns = @JoinColumn(name = "org_id")
    )
    private OrganizationModel organization;

    private Date creationDate = new Date();

}
