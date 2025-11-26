package org.korber.amritesh.repo;

import org.korber.amritesh.model.InventoryBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryBatchRepository extends JpaRepository<InventoryBatch, Long> {
    List<InventoryBatch> findByProductIdOrderByExpiryDateAsc(Long productId);
}
