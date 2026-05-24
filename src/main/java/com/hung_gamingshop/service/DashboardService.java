package com.hung_gamingshop.service;

import com.hung_gamingshop.model.Order;
import com.hung_gamingshop.repository.OrderRepository;
import com.hung_gamingshop.repository.ProductRepository;
import com.hung_gamingshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class DashboardService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Tổng doanh thu
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // Tổng số đơn hàng
        stats.put("totalOrders", orderRepository.count());
        stats.put("pendingOrders", orderRepository.countByStatus(Order.OrderStatus.PENDING));
        stats.put("confirmedOrders", orderRepository.countByStatus(Order.OrderStatus.CONFIRMED));
        stats.put("deliveredOrders", orderRepository.countByStatus(Order.OrderStatus.DELIVERED));

        // Tổng sản phẩm
        stats.put("totalProducts", productRepository.count());

        // Tổng user
        stats.put("totalUsers", userRepository.count());

        // Doanh thu theo ngày (7 ngày gần nhất)
        List<Object[]> revenueByDay = orderRepository.getRevenueByDay();
        List<String> labels = new ArrayList<>();
        List<BigDecimal> revenues = new ArrayList<>();

        int limit = Math.min(revenueByDay.size(), 7);
        for (int i = limit - 1; i >= 0; i--) {
            Object[] row = revenueByDay.get(i);
            labels.add(row[0].toString());
            revenues.add((BigDecimal) row[1]);
        }
        stats.put("chartLabels", labels);
        stats.put("chartRevenues", revenues);

        // Top sản phẩm bán chạy
        List<Object[]> topProducts = productRepository.findTopSellingProducts();
        stats.put("topProducts", topProducts.subList(0, Math.min(topProducts.size(), 5)));

        // 5 đơn hàng mới nhất
        stats.put("recentOrders", orderRepository.findAllByOrderByCreatedAtDesc()
                .stream().limit(5).toList());

        return stats;
    }
}