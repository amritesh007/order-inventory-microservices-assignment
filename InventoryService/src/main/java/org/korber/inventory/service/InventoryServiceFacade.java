package org.korber.inventory.service;


import org.korber.inventory.dto.InventoryBatchDto;
import org.korber.inventory.dto.UpdateInventoryRequest;
import org.korber.inventory.service.handler.InventoryHandlerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceFacade {

    private final InventoryHandlerFactory factory;

    public InventoryServiceFacade(InventoryHandlerFactory factory) {
        this.factory = factory;
    }

    public List<InventoryBatchDto> listBatchesForProduct(Long productId) {
        return factory.getHandler("expiry").listBatches(productId);
    }

    public void updateInventory(UpdateInventoryRequest request) {
        factory.getHandler("expiry").applyUpdate(request);
    }

}
