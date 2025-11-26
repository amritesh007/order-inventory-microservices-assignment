package org.korber.inventory.controller;


import org.junit.jupiter.api.Test;
import org.korber.inventory.dto.InventoryBatchDto;
import org.korber.inventory.model.InventoryBatch;
import org.korber.inventory.model.Product;
import org.korber.inventory.repo.InventoryBatchRepository;
import org.korber.inventory.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryControllerIntegrationTest {

    @Autowired
    TestRestTemplate rest;

    @Autowired
    ProductRepository productRepo;

    @Autowired
    InventoryBatchRepository batchRepo;

    @Test
    void getBatchesReturnsSorted() {
        Product p = productRepo.save(Product.builder().name("TestProd").build());
        batchRepo.save(InventoryBatch.builder().product(p).quantity(5).expiryDate(LocalDate.now().plusDays(10)).build());
        batchRepo.save(InventoryBatch.builder().product(p).quantity(7).expiryDate(LocalDate.now().plusDays(2)).build());

        ResponseEntity<InventoryBatchDto[]> res = rest.getForEntity("/inventory/" + p.getId(), InventoryBatchDto[].class);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        InventoryBatchDto[] arr = res.getBody();
        assertNotNull(arr);
        assertEquals(2, arr.length);
        assertTrue(arr[0].getExpiryDate().isBefore(arr[1].getExpiryDate()));
    }

    @Test
    void updateInventoryDeductsQuantity() {
        Product p = productRepo.save(Product.builder().name("DeductProd").build());
        batchRepo.save(InventoryBatch.builder().product(p).quantity(5).expiryDate(LocalDate.now().plusDays(5)).build());
        batchRepo.save(InventoryBatch.builder().product(p).quantity(10).expiryDate(LocalDate.now().plusDays(20)).build());

        // Deduct 8 -> first batch 5 -> removed; second batch 10->2
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String payload = "{\"productId\":" + p.getId() + ",\"quantity\":8}";
        HttpEntity<String> e = new HttpEntity<>(payload, headers);
        ResponseEntity<Void> res = rest.postForEntity("/inventory/update", e, Void.class);
        assertEquals(HttpStatus.OK, res.getStatusCode());

        // verify totals
        int total = batchRepo.findByProductIdOrderByExpiryDateAsc(p.getId()).stream().mapToInt(InventoryBatch::getQuantity).sum();
        assertEquals(7, total); // 5+10 =15 - 8 =7
    }
}
