package com.inventory.system.stock;

import com.inventory.system.stock.dto.StockInflowRequest;
import com.inventory.system.stock.entity.Stock;

import com.inventory.system.product.entity.Product;
import com.inventory.system.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StockService {
    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Stock addStock(StockInflowRequest request){
        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new RuntimeException("Product not Found"));
        Stock stock = stockRepository.findByProduct(product)
                .orElseGet(() -> {
                    Stock newStock = new Stock();
                    newStock.setProduct(product);
                    newStock.setQuantity(0);
                    return newStock;
                });
        stock.setQuantity(stock.getQuantity() + request.getQuantity());
        return stockRepository.save(stock);
    }

    public Page<Stock> getStocks(String keyword, Pageable pageable){
        if (keyword != null && !keyword.isEmpty()){
            return stockRepository.searchStocks(keyword, pageable);
        }
        return stockRepository.findAll(pageable);
    }
}
