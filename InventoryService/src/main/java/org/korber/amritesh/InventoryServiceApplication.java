package org.korber.amritesh;


import org.korber.amritesh.model.InventoryBatch;
import org.korber.amritesh.model.Product;
import org.korber.amritesh.repo.InventoryBatchRepository;
import org.korber.amritesh.repo.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class);
    }

    @Bean
    CommandLineRunner seed(ProductRepository productRepository, InventoryBatchRepository batchRepository) {
        return args -> {
            Product p1 = productRepository.save(Product.builder().name("Widget-A").build());
            Product p2 = productRepository.save(Product.builder().name("Gadget-B").build());

            batchRepository.save(InventoryBatch.builder()
                    .product(p1)
                    .quantity(100)
                    .expiryDate(LocalDate.now().plusDays(90))
                    .build());

            batchRepository.save(InventoryBatch.builder()
                    .product(p1)
                    .quantity(50)
                    .expiryDate(LocalDate.now().plusDays(30))
                    .build());

            batchRepository.save(InventoryBatch.builder()
                    .product(p2)
                    .quantity(200)
                    .expiryDate(LocalDate.now().plusDays(180))
                    .build());
        };
    }
}