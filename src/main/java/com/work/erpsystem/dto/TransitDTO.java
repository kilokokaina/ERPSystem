package com.work.erpsystem.dto;

import lombok.Data;

@Data
public class TransitDTO {

    private String status;
    private ItemQuantityDTO[] items;
    private Long departPoint;
    private Long arrivePoint;
    private Long orgId;

}
