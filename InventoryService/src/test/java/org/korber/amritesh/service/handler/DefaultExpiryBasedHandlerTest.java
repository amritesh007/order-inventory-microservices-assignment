package org.korber.amritesh.service.handler;

import org.junit.jupiter.api.Test;
import org.korber.amritesh.dto.UpdateInventoryRequest;
import org.korber.amritesh.model.InventoryBatch;
import org.korber.amritesh.model.Product;
import org.korber.amritesh.repo.InventoryBatchRepository;
import org.korber.amritesh.repo.ProductRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultExpiryBasedHandlerTest {

    @Mock
    InventoryBatchRepository batchRepo;

    @Mock
    ProductRepository productRepo;

    @InjectMocks
    DefaultExpiryBasedHandler handler;

    @Test
    void applyUpdateConsumesFromEarliestExpiry() {
        Product p = Product.builder().id(1L).name("P").build();
        InventoryBatch b1 = InventoryBatch.builder().id(10L).product(p).quantity(5).expiryDate(LocalDate.now().plusDays(5)).build();
        InventoryBatch b2 = InventoryBatch.builder().id(11L).product(p).quantity(10).expiryDate(LocalDate.now().plusDays(30)).build();

        when(batchRepo.findByProductIdOrderByExpiryDateAsc(1L)).thenReturn(List.of(b1, b2));

        handler.applyUpdate(new UpdateInventoryRequest(){{
            setProductId(1L);
            setQuantity(12);
        }});

        // after deducting 12: b1 becomes 0, b2 becomes 3
        assertEquals(0, b1.getQuantity());
        assertEquals(3, b2.getQuantity());
        verify(batchRepo, atLeastOnce()).save(any(InventoryBatch.class));
    }

    @Test
    void applyUpdateInsufficientThrows() {
        when(batchRepo.findByProductIdOrderByExpiryDateAsc(1L)).thenReturn(List.of());
        UpdateInventoryRequest r = new UpdateInventoryRequest();
        r.setProductId(1L); r.setQuantity(1);
        assertThrows(IllegalStateException.class, () -> handler.applyUpdate(r));
    }
}

