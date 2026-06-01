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
}
