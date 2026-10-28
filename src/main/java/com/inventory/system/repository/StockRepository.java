package com.inventory.system.repository;

import com.inventory.system.model.Stock;
import com.inventory.system.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProduct(Product product);
    @Query("SELECT s FROM Stock s WHERE LOWER(s.product.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.product.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Stock> searchStocks(@Param("keyword") String keyword, Pageable pageable);
    List<Stock> findByQuantityLessThanEqual(Integer threshold);
}