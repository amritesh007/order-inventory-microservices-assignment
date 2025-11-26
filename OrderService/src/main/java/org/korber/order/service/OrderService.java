package org.korber.order.service;


import org.korber.order.dto.InventoryBatchDto;
import org.korber.order.dto.OrderRequest;
import org.korber.order.dto.UpdateInventoryRequest;
import org.korber.order.model.OrderEntity;
import org.korber.order.model.OrderItem;
import org.korber.order.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
public class OrderService {

    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;
    private final String inventoryBaseUrl;

    public OrderService(RestTemplate restTemplate,
                        OrderRepository orderRepository,
                        @Value("${inventory.service.url}") String inventoryBaseUrl) {
        this.restTemplate = restTemplate;
        this.orderRepository = orderRepository;
        this.inventoryBaseUrl = inventoryBaseUrl;
    }


    public OrderEntity placeOrder(OrderRequest request) {

        String getUrl = inventoryBaseUrl + "/inventory/" + request.getProductId();
        ResponseEntity<InventoryBatchDto[]> resp = restTemplate.getForEntity(getUrl, InventoryBatchDto[].class);
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new RestClientException("Failed to fetch inventory for product " + request.getProductId());
        }

        List<InventoryBatchDto> batches = Arrays.asList(resp.getBody());
        int available = batches.stream().mapToInt(b -> b.getQuantity() == null ? 0 : b.getQuantity()).sum();
        if (available < request.getQuantity()) {
            throw new IllegalStateException("Insufficient stock. Available: " + available + ", requested: " + request.getQuantity());
        }

        UpdateInventoryRequest updateReq = new UpdateInventoryRequest();
        updateReq.setProductId(request.getProductId());
        updateReq.setQuantity(request.getQuantity());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UpdateInventoryRequest> entity = new HttpEntity<>(updateReq, headers);

        restTemplate.exchange(inventoryBaseUrl + "/inventory/update", HttpMethod.POST, entity, Void.class);

        OrderEntity order = OrderEntity.builder()
                .createdAt(Instant.now())
                .status("PLACED")
                .build();

        OrderItem item = OrderItem.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .order(order)
                .build();

        order.getItems().add(item);

        return orderRepository.save(order);
    }
}
