package com.inventory.system.report;

import com.inventory.system.sell.entity.Sell;
import com.inventory.system.sell.entity.SellItem;
import com.inventory.system.stock.entity.Stock;
import com.inventory.system.sell.SellRepository;
import com.inventory.system.stock.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private SellRepository sellRepository;

    // ১. লো স্টক অ্যালার্ট রিপোর্ট (যেমন: স্টক ৫ বা তার কম)
    public List<Stock> getLowStockProducts(int threshold) {
        return stockRepository.findByQuantityLessThanEqualAndDeletedFalse(threshold);
    }

    // ২. টোটাল সেলস এবং প্রফিট অ্যানালিসিস রিপোর্ট
    public Map<String, Object> getSalesAndProfitReport() {
        List<Sell> allSells = sellRepository.findAllByDeletedFalse();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCostOfGoods = BigDecimal.ZERO;

        for (Sell sell : allSells) {
            totalRevenue = totalRevenue.add(sell.getTotalAmount());

            for (SellItem item : sell.getSellItems()) {
                // ক্রয়মূল্য * পরিমাণ
                BigDecimal itemCost = item.getProduct().getPurchasePrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                totalCostOfGoods = totalCostOfGoods.add(itemCost);
            }
        }

        BigDecimal grossProfit = totalRevenue.subtract(totalCostOfGoods);

        Map<String, Object> report = new HashMap<>();
        report.put("totalInvoices", allSells.size());
        report.put("totalRevenue", totalRevenue); // মোট বিক্রি
        report.put("totalCost", totalCostOfGoods); // মোট কেনা দাম
        report.put("grossProfit", grossProfit);   // মোট লাভ বা প্রফিট

        return report;
    }
}