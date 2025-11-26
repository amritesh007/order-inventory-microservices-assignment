package org.korber.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryBatchDto {
    private Long batchId;
    private Long productId;
    private Integer quantity;
    private LocalDate expiryDate;
}

