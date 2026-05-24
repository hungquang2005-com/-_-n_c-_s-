package com.hung_gamingshop.service;

import com.hung_gamingshop.model.*;
import com.hung_gamingshop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CartService cartService;

    // Tạo đơn hàng từ giỏ hàng
    @Transactional
    public Order createOrder(String email, String fullName, String orderEmail,
                             String phone, String address, String note,
                             Order.PaymentMethod paymentMethod) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Cart cart = cartService.getOrCreateCart(email);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống!");
        }

        Order order = new Order();
        order.setUser(user);
        order.setFullName(fullName);
        order.setEmail(orderEmail);
        order.setPhone(phone);
        order.setAddress(address);
        order.setNote(note);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalAmount(cart.getTotalAmount());

        // Nếu thanh toán tiền mặt thì UNPAID, các loại khác giả sử PAID ngay
        if (paymentMethod == Order.PaymentMethod.CASH) {
            order.setPaymentStatus(Order.PaymentStatus.UNPAID);
        } else {
            order.setPaymentStatus(Order.PaymentStatus.PAID);
        }

        Order savedOrder = orderRepository.save(order);

        // Tạo OrderItems từ CartItems
        for (CartItem cartItem : cart.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(cartItem.getProduct());
            item.setProductName(cartItem.getProduct().getName());
            item.setProductImage(cartItem.getProduct().getImageUrl());
            item.setPrice(cartItem.getProduct().getPrice());
            item.setQuantity(cartItem.getQuantity());
            item.setSubtotal(cartItem.getSubtotal());
            orderItemRepository.save(item);
        }

        // Xóa giỏ hàng sau khi đặt
        cartService.clearCart(email);

        return savedOrder;
    }

    // Lịch sử đơn hàng của user
    public List<Order> getUserOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    // Chi tiết đơn hàng
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // Admin: Tất cả đơn hàng
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    // Admin: Cập nhật trạng thái đơn hàng
    @Transactional
    public Order updateStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        order.setStatus(status);
        // Nếu giao hàng thành công thì đánh dấu đã thanh toán (tiền mặt)
        if (status == Order.OrderStatus.DELIVERED) {
            order.setPaymentStatus(Order.PaymentStatus.PAID);
        }
        return orderRepository.save(order);
    }
}