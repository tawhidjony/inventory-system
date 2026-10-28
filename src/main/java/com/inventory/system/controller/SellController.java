package com.inventory.system.controller;

import com.inventory.system.dto.SellRequest;
import com.inventory.system.model.Sell;
import com.inventory.system.service.SellService;
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
        Sell sell = sellService.createSell(request, userEmail);
        return ResponseEntity.ok(sell);
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
}
