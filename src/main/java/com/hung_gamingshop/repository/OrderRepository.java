package com.hung_gamingshop.repository;

import com.hung_gamingshop.model.Order;
import com.hung_gamingshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Lịch sử đơn hàng của user (mới nhất trước)
    List<Order> findByUserOrderByCreatedAtDesc(User user);

    // Tất cả đơn hàng (admin - mới nhất trước)
    List<Order> findAllByOrderByCreatedAtDesc();

    // Tổng doanh thu (admin dashboard)
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.paymentStatus = 'PAID'")
    BigDecimal getTotalRevenue();

    // Doanh thu theo ngày (admin dashboard)
    @Query("SELECT DATE(o.createdAt), COALESCE(SUM(o.totalAmount), 0) " +
           "FROM Order o WHERE o.paymentStatus = 'PAID' " +
           "GROUP BY DATE(o.createdAt) ORDER BY DATE(o.createdAt) DESC")
    List<Object[]> getRevenueByDay();

    @Query("SELECT DATE(o.createdAt), COUNT(o.id) " +
           "FROM Order o GROUP BY DATE(o.createdAt) ORDER BY DATE(o.createdAt) DESC")
    List<Object[]> getOrdersByDay();

    // Tổng số đơn hàng
    long countByStatus(Order.OrderStatus status);

    // ── REVENUE PAGE ──────────────────────────────────────────────

    // Doanh thu từng ngày trong 1 tháng cụ thể (ví dụ: tháng 5/2026)
    @Query("SELECT DAY(o.createdAt), COALESCE(SUM(o.totalAmount), 0) " +
           "FROM Order o WHERE o.paymentStatus = 'PAID' " +
           "AND YEAR(o.createdAt) = :year AND MONTH(o.createdAt) = :month " +
           "GROUP BY DAY(o.createdAt) ORDER BY DAY(o.createdAt) ASC")
    List<Object[]> getRevenueByDayInMonth(@org.springframework.data.repository.query.Param("year") int year,
                                          @org.springframework.data.repository.query.Param("month") int month);

    // Doanh thu từng tháng trong 1 năm cụ thể (ví dụ: năm 2026)
    @Query("SELECT MONTH(o.createdAt), COALESCE(SUM(o.totalAmount), 0) " +
           "FROM Order o WHERE o.paymentStatus = 'PAID' " +
           "AND YEAR(o.createdAt) = :year " +
           "GROUP BY MONTH(o.createdAt) ORDER BY MONTH(o.createdAt) ASC")
    List<Object[]> getRevenueByMonthInYear(@org.springframework.data.repository.query.Param("year") int year);

    // Doanh thu từng năm (tất cả)
    @Query("SELECT YEAR(o.createdAt), COALESCE(SUM(o.totalAmount), 0) " +
           "FROM Order o WHERE o.paymentStatus = 'PAID' " +
           "GROUP BY YEAR(o.createdAt) ORDER BY YEAR(o.createdAt) ASC")
    List<Object[]> getRevenueByYear();

    // Tổng đơn + doanh thu trong tháng (cho stat card)
    @Query("SELECT COUNT(o.id), COALESCE(SUM(o.totalAmount), 0) " +
           "FROM Order o WHERE o.paymentStatus = 'PAID' " +
           "AND YEAR(o.createdAt) = :year AND MONTH(o.createdAt) = :month")
    List<Object[]> getMonthSummary(@org.springframework.data.repository.query.Param("year") int year,
                                   @org.springframework.data.repository.query.Param("month") int month);

    // Danh sách năm có dữ liệu
    @Query("SELECT DISTINCT YEAR(o.createdAt) FROM Order o ORDER BY YEAR(o.createdAt) DESC")
    List<Integer> getDistinctYears();
}