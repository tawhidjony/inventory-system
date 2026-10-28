package com.inventory.system.sell.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inventory.system.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;

@Entity
@Table(name = "sell_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sell_id")
    @JsonIgnore // ব্যাক-রেফারেন্স JSON এ গেলে অসীম লুপ হয়
    @ToString.Exclude
    private Sell sell;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;
}
