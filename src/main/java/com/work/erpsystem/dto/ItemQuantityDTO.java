package com.work.erpsystem.dto;

import lombok.Data;

@Data
public class ItemQuantityDTO {

    private Long itemId;
    private String itemName;
    private Double itemPrice;
    private int quantity;

}
