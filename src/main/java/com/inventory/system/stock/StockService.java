package com.inventory.system.stock;

import com.inventory.system.common.exception.ResourceNotFoundException;
import com.inventory.system.product.ProductRepository;
import com.inventory.system.product.entity.Product;
import com.inventory.system.stock.dto.StockInflowRequest;
import com.inventory.system.stock.dto.StockUpdateRequest;
import com.inventory.system.stock.entity.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {
    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;

    // স্টক ইনফ্লো: আগের পরিমাণের সাথে যোগ হয়
    @Transactional
    public Stock addStock(StockInflowRequest request) {
        Product product = productRepository.findByIdAndDeletedFalse(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));
        Stock stock = stockRepository.findByProductAndDeletedFalse(product)
                .orElseGet(() -> {
                    Stock newStock = new Stock();
                    newStock.setProduct(product);
                    newStock.setQuantity(0);
                    return newStock;
                });
        stock.setQuantity(stock.getQuantity() + request.getQuantity());
        return stockRepository.save(stock);
    }

    public Page<Stock> getStocks(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty()) {
            return stockRepository.searchStocks(keyword, pageable);
        }
        return stockRepository.findAllByDeletedFalse(pageable);
    }

    public Stock getStockById(Long id) {
        return stockRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found with id: " + id));
    }

    // স্টক অ্যাডজাস্টমেন্ট: পরিমাণ সরাসরি সেট করা (যেমন: ফিজিক্যাল কাউন্টের পর)
    @Transactional
    public Stock updateStock(Long id, StockUpdateRequest request) {
        Stock stock = getStockById(id);
        stock.setQuantity(request.getQuantity());
        return stockRepository.save(stock);
    }

    @Transactional
    public void deleteStock(Long id) {
        Stock stock = getStockById(id);
        stock.setDeleted(true);
        stockRepository.save(stock);
    }
}
