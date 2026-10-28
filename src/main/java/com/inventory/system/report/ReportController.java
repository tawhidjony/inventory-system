package com.inventory.system.report;

import com.inventory.system.stock.entity.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // লো স্টক রিপোর্ট দেখার API (সুপার এডমিন বা নির্দিষ্ট পারমিশন হোল্ডার)
    @GetMapping("/low-stock")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'READ_PRODUCT')")
    public ResponseEntity<List<Stock>> getLowStockReport(@RequestParam(defaultValue = "5") int threshold) {
        List<Stock> lowStockItems = reportService.getLowStockProducts(threshold);
        return ResponseEntity.ok(lowStockItems);
    }

    // সেলস ও প্রফিট/লস অ্যানালিসিস রিপোর্ট API
    @GetMapping("/profit-loss")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'READ_SELL')")
    public ResponseEntity<Map<String, Object>> getProfitLossReport() {
        Map<String, Object> report = reportService.getSalesAndProfitReport();
        return ResponseEntity.ok(report);
    }
}