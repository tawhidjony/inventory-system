package com.inventory.system.sell.entity;

import com.inventory.system.common.entity.SoftDeletableEntity;
import com.inventory.system.customer.entity.Customer;
import com.inventory.system.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sells")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Sell extends SoftDeletableEntity {
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

    // orphanRemoval: আইটেম লিস্ট থেকে বাদ দিলে DB থেকেও মুছে যায় (সেল এডিটের জন্য দরকার)
    @OneToMany(mappedBy = "sell", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SellItem> sellItems = new ArrayList<>();
}
