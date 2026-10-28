package com.inventory.system.sell;

import com.inventory.system.sell.entity.Sell;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SellRepository extends JpaRepository<Sell, Long> {
    List<Sell> findBySellDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}