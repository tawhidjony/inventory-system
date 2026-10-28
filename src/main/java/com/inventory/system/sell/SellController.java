package com.inventory.system.sell;

import com.inventory.system.sell.dto.SellRequest;
import com.inventory.system.sell.entity.Sell;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sells")
public class SellController {
    @Autowired
    private SellService sellService;

    // নতুন সেল বা ইনভয়েস তৈরি করা (স্টক অটো ডিডাক্ট হবে)
    @PostMapping
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'CREATE_SELL')")
    public ResponseEntity<Sell> createSell(@Valid @RequestBody SellRequest request, Authentication authentication) {
        String userEmail = authentication.getName(); // লগইন করা ইউজারের ইমেল
        return ResponseEntity.ok(sellService.createSell(request, userEmail));
    }

    // সেলস হিস্ট্রি পেজিনেশনসহ দেখা
    @GetMapping
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'READ_SELL')")
    public ResponseEntity<Page<Sell>> getSells(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(sellService.getSells(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'READ_SELL')")
    public ResponseEntity<Sell> getSellById(@PathVariable Long id) {
        return ResponseEntity.ok(sellService.getSellById(id));
    }

    // সেল এডিট (আগের স্টক ফেরত দিয়ে নতুন আইটেম অনুযায়ী স্টক কাটা হয়)
    @PutMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'CREATE_SELL')")
    public ResponseEntity<Sell> updateSell(@PathVariable Long id, @Valid @RequestBody SellRequest request) {
        return ResponseEntity.ok(sellService.updateSell(id, request));
    }

    // সেল বাতিল / সফট ডিলিট (স্টক ফেরত যায়)
    @DeleteMapping("/{id}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'CREATE_SELL')")
    public ResponseEntity<Void> deleteSell(@PathVariable Long id) {
        sellService.deleteSell(id);
        return ResponseEntity.noContent().build();
    }
}
