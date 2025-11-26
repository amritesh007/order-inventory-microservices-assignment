package org.korber.inventory.service.handler;

import org.korber.inventory.dto.InventoryBatchDto;
import org.korber.inventory.dto.UpdateInventoryRequest;

import java.util.List;

public interface InventoryHandler {

    List<InventoryBatchDto> listBatches(Long productId);
    void applyUpdate(UpdateInventoryRequest updateInventoryRequest);
}
