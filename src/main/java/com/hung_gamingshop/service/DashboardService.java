package com.hung_gamingshop.service;

import com.hung_gamingshop.model.Order;
import com.hung_gamingshop.repository.OrderRepository;
import com.hung_gamingshop.repository.ProductRepository;
import com.hung_gamingshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
        stats.put("chartMaxRevenue", revenues.stream()
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ONE));

        List<Object[]> ordersByDay = orderRepository.getOrdersByDay();
        List<String> orderLabels = new ArrayList<>();
        List<Long> orderCounts = new ArrayList<>();
        int orderLimit = Math.min(ordersByDay.size(), 7);
        for (int i = orderLimit - 1; i >= 0; i--) {
            Object[] row = ordersByDay.get(i);
            orderLabels.add(row[0].toString());
            orderCounts.add(((Number) row[1]).longValue());
        }
        stats.put("orderChartLabels", orderLabels);
        stats.put("orderChartCounts", orderCounts);
        stats.put("orderChartMax", orderCounts.stream()
                .max(Long::compareTo)
                .orElse(1L));

        // Top sản phẩm bán chạy
        List<Object[]> topProducts = productRepository.findTopSellingProducts();
        stats.put("topProducts", topProducts.subList(0, Math.min(topProducts.size(), 5)));

        // 5 đơn hàng mới nhất
        stats.put("recentOrders", orderRepository.findAllByOrderByCreatedAtDesc()
                .stream().limit(5).toList());

        return stats;
    }

    // ── TRANG DOANH THU ──────────────────────────────────────────

    public Map<String, Object> getRevenuePageStats(String viewType, int year, int month) {
        Map<String, Object> stats = new HashMap<>();

        int currentYear  = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        // Danh sách năm có dữ liệu để hiển thị dropdown
        List<Integer> availableYears = orderRepository.getDistinctYears();
        if (availableYears.isEmpty()) availableYears = List.of(currentYear);
        stats.put("availableYears", availableYears);
        stats.put("selectedYear",  year);
        stats.put("selectedMonth", month);
        stats.put("viewType",      viewType);

        List<String>     labels   = new ArrayList<>();
        List<BigDecimal> revenues = new ArrayList<>();

        switch (viewType) {

            case "day": {
                // Doanh thu từng ngày trong tháng được chọn
                List<Object[]> rows = orderRepository.getRevenueByDayInMonth(year, month);
                Map<Integer, BigDecimal> map = new LinkedHashMap<>();
                for (Object[] r : rows) {
                    map.put(((Number) r[0]).intValue(), (BigDecimal) r[1]);
                }
                int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
                for (int d = 1; d <= daysInMonth; d++) {
                    labels.add(String.format("%02d/%02d", d, month));
                    revenues.add(map.getOrDefault(d, BigDecimal.ZERO));
                }

                // Stat cards cho tháng — lấy phần tử đầu của List
                List<Object[]> summaryList = orderRepository.getMonthSummary(year, month);
                if (summaryList != null && !summaryList.isEmpty()) {
                    Object[] summary = summaryList.get(0);
                    stats.put("summaryOrders",  ((Number) summary[0]).longValue());
                    stats.put("summaryRevenue", summary[1] != null ? (BigDecimal) summary[1] : BigDecimal.ZERO);
                } else {
                    stats.put("summaryOrders",  0L);
                    stats.put("summaryRevenue", BigDecimal.ZERO);
                }
                stats.put("chartTitle", "Doanh thu theo ngày — "
                        + String.format("%02d", month) + "/" + year);
                break;
            }

            case "month": {
                // Doanh thu từng tháng trong năm được chọn
                List<Object[]> rows = orderRepository.getRevenueByMonthInYear(year);
                Map<Integer, BigDecimal> map = new LinkedHashMap<>();
                for (Object[] r : rows) {
                    map.put(((Number) r[0]).intValue(), (BigDecimal) r[1]);
                }
                for (int m = 1; m <= 12; m++) {
                    labels.add("Tháng " + m);
                    revenues.add(map.getOrDefault(m, BigDecimal.ZERO));
                }

                BigDecimal yearTotal = revenues.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                stats.put("summaryRevenue", yearTotal);
                // Đếm số tháng có phát sinh doanh thu trong năm
                stats.put("summaryOrders",  (long) rows.size());
                stats.put("chartTitle", "Doanh thu theo tháng — năm " + year);
                break;
            }

            case "year":
            default: {
                // Doanh thu từng năm
                List<Object[]> rows = orderRepository.getRevenueByYear();
                for (Object[] r : rows) {
                    labels.add("Năm " + r[0]);
                    revenues.add((BigDecimal) r[1]);
                }
                BigDecimal total = revenues.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                stats.put("summaryRevenue", total);
                stats.put("summaryOrders",  orderRepository.count());
                stats.put("chartTitle", "Doanh thu theo năm");
                break;
            }
        }

        stats.put("chartLabels",   labels);
        stats.put("chartRevenues", revenues);

        // Tổng doanh thu toàn bộ (stat card góc)
        stats.put("totalRevenue", orderRepository.getTotalRevenue());

        return stats;
    }
}