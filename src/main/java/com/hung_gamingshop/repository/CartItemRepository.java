package com.hung_gamingshop.repository;

import com.hung_gamingshop.model.Cart;
import com.hung_gamingshop.model.CartItem;
import com.hung_gamingshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    void deleteByCartId(Long cartId);
}
