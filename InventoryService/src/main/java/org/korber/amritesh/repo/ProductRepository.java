package org.korber.amritesh.repo;

import org.korber.amritesh.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
