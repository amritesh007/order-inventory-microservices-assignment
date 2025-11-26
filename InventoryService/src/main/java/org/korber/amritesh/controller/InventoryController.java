package org.korber.amritesh.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.korber.amritesh.dto.InventoryBatchDto;
import org.korber.amritesh.dto.UpdateInventoryRequest;
import org.korber.amritesh.service.InventoryServiceFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@Tag(name = "Inventory API", description = "Operations related to product inventory and batches")
public class InventoryController {
    private final InventoryServiceFacade facade;

    public InventoryController(InventoryServiceFacade facade) {
        this.facade = facade;
    }

    @Operation(
            summary = "Get inventory batches",
            description = "Returns a list of inventory batches for a product sorted by expiry date"
    )
    @GetMapping("/{productId}")
    public ResponseEntity<List<InventoryBatchDto>> getBatches(@PathVariable Long productId) {
        return ResponseEntity.ok(facade.listBatchesForProduct(productId));
    }

    @Operation(
            summary = "Update inventory",
            description = "Deducts quantity from product inventory after an order is placed"
    )
    @PostMapping("/update")
    public ResponseEntity<Void> updateInventory(@RequestBody UpdateInventoryRequest request) {
        facade.updateInventory(request);
        return ResponseEntity.ok().build();
    }
}
