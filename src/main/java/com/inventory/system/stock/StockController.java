package com.inventory.system.stock;

import com.inventory.system.stock.dto.StockInflowRequest;
import com.inventory.system.stock.dto.StockUpdateRequest;
import com.inventory.system.stock.entity.Stock;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    @Autowired
    private StockService stockService;

    @PostMapping("/inflow")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'MANAGE_STOCK')")
    public ResponseEntity<Stock> addStock(@Valid @RequestBody StockInflowRequest request) {
        return ResponseEntity.ok(stockService.addStock(request));
    }

    @GetMapping
    public ResponseEntity<Page<Stock>> getStocks(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ResponseEntity.ok(stockService.getStocks(keyword, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.getStockById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'MANAGE_STOCK')")
    public ResponseEntity<Stock> updateStock(@PathVariable Long id, @Valid @RequestBody StockUpdateRequest request) {
        return ResponseEntity.ok(stockService.updateStock(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'MANAGE_STOCK')")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }
}
