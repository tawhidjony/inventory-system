package com.inventory.system.product;


import com.inventory.system.stock.Stock;
import com.inventory.system.stock.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;


    @Transactional
    public Product saveProduct(Product product){
        Product saveProduct = productRepository.save(product);

        Stock stock = new Stock();
        stock.setProduct(saveProduct);
        stock.setQuantity(0);
        stockRepository.save(stock);

        return saveProduct;
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
}
