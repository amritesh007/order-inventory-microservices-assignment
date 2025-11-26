package org.korber.amritesh.service.handler;


import org.korber.amritesh.dto.InventoryBatchDto;
import org.korber.amritesh.dto.UpdateInventoryRequest;
import org.korber.amritesh.model.InventoryBatch;
import org.korber.amritesh.repo.InventoryBatchRepository;
import org.korber.amritesh.repo.ProductRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component("expiry")
public class DefaultExpiryBasedHandler implements InventoryHandler{

    private final InventoryBatchRepository inventoryBatchRepository;
    private final ProductRepository productRepository;

    public DefaultExpiryBasedHandler(InventoryBatchRepository inventoryBatchRepository, ProductRepository productRepository) {
        this.inventoryBatchRepository = inventoryBatchRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<InventoryBatchDto> listBatches(Long productId) {
        return inventoryBatchRepository.findByProductIdOrderByExpiryDateAsc(productId)
                .stream()
                .map(b -> InventoryBatchDto.builder()
                        .batchId(b.getId())
                        .productId(b.getProduct().getId())
                        .quantity(b.getQuantity())
                        .expiryDate(b.getExpiryDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void applyUpdate(UpdateInventoryRequest updateRequest) {
        Long productId = updateRequest.getProductId();
        Integer toDeduct = updateRequest.getQuantity();
        if (toDeduct == null || toDeduct <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }

        List<InventoryBatch> batches = inventoryBatchRepository.findByProductIdOrderByExpiryDateAsc(productId);

        int remaining = toDeduct;
        for (InventoryBatch b : batches) {
            if (remaining <= 0) break;
            int avail = b.getQuantity() == null ? 0 : b.getQuantity();
            if (avail <= 0) continue;

            if (avail > remaining) {
                b.setQuantity(avail - remaining);
                remaining = 0;
                inventoryBatchRepository.save(b);
            } else {
                remaining -= avail;
                b.setQuantity(0);
                inventoryBatchRepository.save(b);
            }
        }

        if (remaining > 0) {
            throw new IllegalStateException("Insufficient inventory. Short by " + remaining);
        }
    }
}
