package org.korber.order.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.korber.order.dto.InventoryBatchDto;
import org.korber.order.dto.OrderRequest;
import org.korber.order.model.OrderEntity;
import org.korber.order.model.OrderItem;
import org.korber.order.repo.OrderRepository;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class OrderServiceTest {

    @Mock RestTemplate restTemplate;
    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderService orderService;

    @Test
    void placeOrder_success() {
        OrderRequest req = new OrderRequest();
        req.setProductId(1L);
        req.setQuantity(5);

        InventoryBatchDto[] batches = new InventoryBatchDto[] {
                new InventoryBatchDto(10L, 1L, 3, LocalDate.now().plusDays(5)),
                new InventoryBatchDto(11L, 1L, 5, LocalDate.now().plusDays(20))
        };

        when(restTemplate.getForEntity(contains("/inventory/1"), eq(InventoryBatchDto[].class)))
                .thenReturn(new ResponseEntity<>(batches, HttpStatus.OK));

        when(restTemplate.exchange(contains("/inventory/update"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));

        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(i -> i.getArgument(0));

        OrderEntity order = orderService.placeOrder(req);

        assertEquals("PLACED", order.getStatus());
        assertNotNull(order.getItems());
        assertEquals(1, order.getItems().size());
        OrderItem item = order.getItems().get(0);
        assertEquals(1L, item.getProductId());
        assertEquals(5, item.getQuantity());
        verify(orderRepository).save(any(OrderEntity.class));
    }

    @Test
    void placeOrder_insufficientStock_throws() {
        OrderRequest req = new OrderRequest();
        req.setProductId(1L);
        req.setQuantity(10);

        InventoryBatchDto[] batches = new InventoryBatchDto[] {
                new InventoryBatchDto(10L, 1L, 3, LocalDate.now().plusDays(5))
        };

        when(restTemplate.getForEntity(anyString(), eq(InventoryBatchDto[].class)))
                .thenReturn(new ResponseEntity<>(batches, HttpStatus.OK));

        assertThrows(IllegalStateException.class, () -> orderService.placeOrder(req));
        verify(orderRepository, never()).save(any());
    }
}
