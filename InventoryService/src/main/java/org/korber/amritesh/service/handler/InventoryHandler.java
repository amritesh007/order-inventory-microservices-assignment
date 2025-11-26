package org.korber.amritesh.service.handler;

import org.korber.amritesh.dto.InventoryBatchDto;
import org.korber.amritesh.dto.UpdateInventoryRequest;

import java.util.List;

public interface InventoryHandler {

    List<InventoryBatchDto> listBatches(Long productId);
    void applyUpdate(UpdateInventoryRequest updateInventoryRequest);
}
