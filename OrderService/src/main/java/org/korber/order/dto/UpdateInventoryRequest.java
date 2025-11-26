package org.korber.order.dto;

import lombok.Data;

@Data
public class UpdateInventoryRequest {
    private Long productId;
    private Integer quantity;
}

