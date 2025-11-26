package org.korber.order.controller;

import org.korber.order.dto.InventoryBatchDto;
import org.korber.order.repo.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.korber.order.util.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.springframework.test.web.client.ExpectedCount.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired RestTemplate restTemplate;
    @Autowired OrderRepository orderRepository;

    MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        orderRepository.deleteAll();
    }

    @Test
    void postOrder_happyPath_persists() throws Exception {
        InventoryBatchDto[] batches = new InventoryBatchDto[] {
                new InventoryBatchDto(1L, 1L, 10, LocalDate.now().plusDays(10))
        };

        mockServer.expect(once(), requestTo("http://localhost:8081/inventory/1"))
                .andRespond(withSuccess(TestUtils.toJson(batches), MediaType.APPLICATION_JSON));

        mockServer.expect(once(), requestTo("http://localhost:8081/inventory/update"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withSuccess());

        String payload = "{\"productId\":1,\"quantity\":5}";
        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        mockServer.verify();
    }
}
