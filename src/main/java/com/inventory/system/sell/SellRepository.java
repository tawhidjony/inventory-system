package com.inventory.system.sell;

import com.inventory.system.sell.entity.Sell;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SellRepository extends JpaRepository<Sell, Long> {
    List<Sell> findBySellDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Sell> findAllByDeletedFalse();
    Page<Sell> findAllByDeletedFalse(Pageable pageable);
    Optional<Sell> findByIdAndDeletedFalse(Long id);
}
