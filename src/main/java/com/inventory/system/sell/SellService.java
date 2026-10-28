package com.inventory.system.sell;

import com.inventory.system.sell.dto.SellRequest;
import com.inventory.system.sell.entity.Sell;
import com.inventory.system.sell.entity.SellItem;

import com.inventory.system.customer.entity.Customer;
import com.inventory.system.customer.CustomerRepository;
import com.inventory.system.product.entity.Product;
import com.inventory.system.product.ProductRepository;
import com.inventory.system.stock.entity.Stock;
import com.inventory.system.stock.StockRepository;
import com.inventory.system.user.entity.User;
import com.inventory.system.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SellService {
    @Autowired
    private SellRepository sellRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Transactional
    public Sell createSell(SellRequest request, String userEmail){
        Customer customer = customerRepository.findById(request.getCustomerId()).orElseThrow(() -> new RuntimeException("Customer not found"));
        User seller = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("Seller not found"));

        Sell sell = new Sell();
        sell.setCustomer(customer);
        sell.setSellDate(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<SellItem> sellItems = new ArrayList<>();

        for (var itemReq: request.getItems()){
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemReq.getProductId()));

            Stock stock = stockRepository.findByProduct(product)
                    .orElseThrow(() -> new RuntimeException("Stock not found for product: " + product.getName()));

            // স্টক চেক করা পর্যাপ্ত আছে কি না
            if (stock.getQuantity() < itemReq.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // স্টক কমানো (Stock Out)
            stock.setQuantity(stock.getQuantity() - itemReq.getQuantity());
            stockRepository.save(stock);

            // সাবটোটাল হিসাব করা (Selling Price * Quantity)
            BigDecimal subTotal = product.getSellingPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(subTotal);

            SellItem sellItem = new SellItem();
            sellItem.setSell(sell);
            sellItem.setProduct(product);
            sellItem.setQuantity(itemReq.getQuantity());
            sellItem.setUnitPrice(product.getSellingPrice());
            sellItem.setSubTotal(subTotal);

            sellItems.add(sellItem);
        }
        sell.setTotalAmount(totalAmount);
        sell.setSellItems(sellItems);

        return sellRepository.save(sell);
    }

    public Page<Sell> getSells(Pageable pageable) {
        return sellRepository.findAll(pageable);
    }


}
