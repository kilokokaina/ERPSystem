package com.work.erpsystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class ItemModel {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long itemId;

    private String itemName;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String itemDescribe;

    private Date itemCreationDate = new Date();

    @OneToMany
    @JoinTable(
            name = "item_images", joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private List<FileModel> itemImages;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryModel categoryModel;

    @ManyToOne
    @JoinColumn(name = "org_id")
    private OrganizationModel organizationModel;

    @OneToOne
    private BarcodeModel barcode;

    private Double itemPurchasePrice;

}
