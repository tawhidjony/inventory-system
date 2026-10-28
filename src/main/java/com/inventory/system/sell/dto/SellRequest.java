package com.inventory.system.sell.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SellRequest {

    @NotNull(message = "Customer Id is required")
    private Long customerId;

    @NotNull(message = "Sell Items cannot be empty")
    private List<SellItemRequest> items;
}

