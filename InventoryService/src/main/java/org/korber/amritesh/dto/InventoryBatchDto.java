package org.korber.amritesh.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class InventoryBatchDto {
    private Long batchId;
    private Long productId;
    private Integer quantity;
    private LocalDate expiryDate;
}
