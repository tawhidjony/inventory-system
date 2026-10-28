package com.inventory.system.sell;

import com.inventory.system.customer.Customer;
import com.inventory.system.user.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sells")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sell {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User soldBy; // কোন ইউজার বিক্রি করেছে

    private BigDecimal totalAmount;

    private LocalDateTime sellDate = LocalDateTime.now();

    @OneToMany(mappedBy = "sell", cascade = CascadeType.ALL)
    private List<SellItem> sellItems;
}