package com.inventory.system.stock;

import com.inventory.system.product.entity.Product;
import com.inventory.system.stock.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProduct(Product product);
    Optional<Stock> findByProductAndDeletedFalse(Product product);
    Optional<Stock> findByIdAndDeletedFalse(Long id);
    Page<Stock> findAllByDeletedFalse(Pageable pageable);

    @Query("SELECT s FROM Stock s WHERE s.deleted = false AND (LOWER(s.product.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.product.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Stock> searchStocks(@Param("keyword") String keyword, Pageable pageable);

    List<Stock> findByQuantityLessThanEqualAndDeletedFalse(Integer threshold);
}
