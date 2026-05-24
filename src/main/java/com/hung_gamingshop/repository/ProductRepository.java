package com.hung_gamingshop.repository;

import com.hung_gamingshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Sản phẩm nổi bật trang chủ
    List<Product> findByIsFeaturedTrue();

    // Tìm kiếm theo tên hoặc brand hoặc category
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    // Lọc theo category
    List<Product> findByCategory(String category);

    // Top sản phẩm bán chạy (dùng cho admin dashboard)
    @Query("SELECT p.name, SUM(oi.quantity) as totalSold " +
           "FROM OrderItem oi JOIN oi.product p " +
           "GROUP BY p.id, p.name ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProducts();

    // Tất cả categories có sản phẩm
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL")
    List<String> findAllCategories();
}