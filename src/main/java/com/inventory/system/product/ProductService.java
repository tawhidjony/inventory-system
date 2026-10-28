package com.inventory.system.product;

import com.inventory.system.common.exception.ResourceNotFoundException;
import com.inventory.system.product.dto.ProductRequest;
import com.inventory.system.product.entity.Product;
import com.inventory.system.stock.StockRepository;
import com.inventory.system.stock.entity.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    // নতুন প্রোডাক্ট তৈরি (সাথে ০ কোয়ান্টিটির স্টক রো তৈরি হয়)
    @Transactional
    public Product createProduct(ProductRequest request) {
        productRepository.findBySku(request.getSku()).ifPresent(existing -> {
            throw new RuntimeException("Product already exists with sku: " + request.getSku());
        });

        Product product = new Product();
        applyRequest(product, request);
        Product savedProduct = productRepository.save(product);

        Stock stock = new Stock();
        stock.setProduct(savedProduct);
        stock.setQuantity(0);
        stockRepository.save(stock);

        return savedProduct;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAllByDeletedFalse();
    }

    public Product getProductById(Long id) {
        return productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {
        Product product = getProductById(id);

        productRepository.findBySku(request.getSku())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new RuntimeException("Product already exists with sku: " + request.getSku());
                });

        applyRequest(product, request);
        return productRepository.save(product);
    }

    // সফট ডিলিট: প্রোডাক্ট এবং তার স্টক রো দুটোই deleted = true হয়
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        product.setDeleted(true);
        productRepository.save(product);

        stockRepository.findByProductAndDeletedFalse(product).ifPresent(stock -> {
            stock.setDeleted(true);
            stockRepository.save(stock);
        });
    }

    private void applyRequest(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setPurchasePrice(request.getPurchasePrice());
        product.setSellingPrice(request.getSellingPrice());
    }
}
