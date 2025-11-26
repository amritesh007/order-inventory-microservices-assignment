package org.korber.order.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.korber.order.dto.OrderRequest;
import org.korber.order.model.OrderEntity;
import org.korber.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Tag(name = "Order API", description = "Endpoints for placing product orders")
public class OrderController {

    private final OrderService svc;

    public OrderController(OrderService svc) {
        this.svc = svc;
    }

    @Operation(
            summary = "Place a new order",
            description = "Validates product stock with Inventory Service, deducts quantity, and saves the order"
    )
    @PostMapping
    public ResponseEntity<OrderEntity> placeOrder(@RequestBody OrderRequest request) {
        OrderEntity saved = svc.placeOrder(request);
        return ResponseEntity.ok(saved);
    }
}