package com.inventory.system.sell;

import com.inventory.system.common.exception.ResourceNotFoundException;
import com.inventory.system.customer.CustomerRepository;
import com.inventory.system.customer.entity.Customer;
import com.inventory.system.product.ProductRepository;
import com.inventory.system.product.entity.Product;
import com.inventory.system.sell.dto.SellItemRequest;
import com.inventory.system.sell.dto.SellRequest;
import com.inventory.system.sell.entity.Sell;
import com.inventory.system.sell.entity.SellItem;
import com.inventory.system.stock.StockRepository;
import com.inventory.system.stock.entity.Stock;
import com.inventory.system.user.UserRepository;
import com.inventory.system.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    // নতুন সেল: প্রতিটি আইটেমের জন্য স্টক কমে
    @Transactional
    public Sell createSell(SellRequest request, String userEmail) {
        Customer customer = findCustomer(request.getCustomerId());
        User seller = userRepository.findByEmailAndDeletedFalse(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        Sell sell = new Sell();
        sell.setCustomer(customer);
        sell.setSoldBy(seller);
        sell.setSellDate(LocalDateTime.now());

        applyItems(sell, request.getItems());
        return sellRepository.save(sell);
    }

    public Page<Sell> getSells(Pageable pageable) {
        return sellRepository.findAllByDeletedFalse(pageable);
    }

    public Sell getSellById(Long id) {
        return sellRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sell not found with id: " + id));
    }

    // সেল এডিট: আগের আইটেমের স্টক ফেরত দিয়ে নতুন আইটেম অনুযায়ী আবার স্টক কাটা হয়
    @Transactional
    public Sell updateSell(Long id, SellRequest request) {
        Sell sell = getSellById(id);
        sell.setCustomer(findCustomer(request.getCustomerId()));

        restoreStock(sell);
        sell.getSellItems().clear(); // orphanRemoval পুরনো আইটেম মুছে দেয়
        applyItems(sell, request.getItems());

        return sellRepository.save(sell);
    }

    // সেল বাতিল (সফট ডিলিট): স্টক ফেরত যায়, ইনভয়েস রেকর্ড থেকে যায়
    @Transactional
    public void deleteSell(Long id) {
        Sell sell = getSellById(id);
        restoreStock(sell);
        sell.setDeleted(true);
        sellRepository.save(sell);
    }

    private Customer findCustomer(Long customerId) {
        return customerRepository.findByIdAndDeletedFalse(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
    }

    private void applyItems(Sell sell, List<SellItemRequest> items) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SellItemRequest itemReq : items) {
            Product product = productRepository.findByIdAndDeletedFalse(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemReq.getProductId()));

            Stock stock = stockRepository.findByProductAndDeletedFalse(product)
                    .orElseThrow(() -> new ResourceNotFoundException("Stock not found for product: " + product.getName()));

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

            sell.getSellItems().add(sellItem);
        }
        sell.setTotalAmount(totalAmount);
    }

    // সেলের আইটেমগুলোর পরিমাণ স্টকে ফেরত দেওয়া (ডিলিটেড প্রোডাক্টের স্টকেও ফেরত যায়)
    private void restoreStock(Sell sell) {
        for (SellItem item : sell.getSellItems()) {
            stockRepository.findByProduct(item.getProduct()).ifPresent(stock -> {
                stock.setQuantity(stock.getQuantity() + item.getQuantity());
                stockRepository.save(stock);
            });
        }
    }
}
