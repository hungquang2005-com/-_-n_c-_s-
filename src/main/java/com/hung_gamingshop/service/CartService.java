package com.hung_gamingshop.service;

import com.hung_gamingshop.model.*;
import com.hung_gamingshop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;

    // Lấy hoặc tạo giỏ hàng cho user
    public Cart getOrCreateCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    // Thêm sản phẩm vào giỏ
    @Transactional
    public void addToCart(String email, Long productId, int quantity) {
        Cart cart = getOrCreateCart(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    // Cập nhật số lượng
    @Transactional
    public void updateQuantity(String email, Long itemId, int quantity) {
        Cart cart = getOrCreateCart(email);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Không có quyền thay đổi giỏ hàng này");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    // Xóa sản phẩm khỏi giỏ
    @Transactional
    public void removeItem(String email, Long itemId) {
        Cart cart = getOrCreateCart(email);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Không có quyền xóa sản phẩm này");
        }
        cartItemRepository.delete(item);
    }

    // Xóa toàn bộ giỏ hàng (sau khi đặt hàng)
    @Transactional
    public void clearCart(String email) {
        Cart cart = getOrCreateCart(email);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    // Tổng số sản phẩm trong giỏ (để hiển thị trên navbar)
    public int getCartItemCount(String email) {
        try {
            Cart cart = getOrCreateCart(email);
            return cart.getTotalQuantity();
        } catch (Exception e) {
            return 0;
        }
    }
}