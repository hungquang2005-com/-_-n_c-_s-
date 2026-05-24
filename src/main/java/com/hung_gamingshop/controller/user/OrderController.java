package com.hung_gamingshop.controller.user;

import com.hung_gamingshop.model.Order;
import com.hung_gamingshop.service.CartService;
import com.hung_gamingshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired private OrderService orderService;
    @Autowired private CartService cartService;

    // Lịch sử đơn hàng
    @GetMapping
    public String history(Model model, Authentication auth) {
        model.addAttribute("orders", orderService.getUserOrders(auth.getName()));
        model.addAttribute("cartCount", cartService.getCartItemCount(auth.getName()));
        return "user/order-history";
    }

    // Chi tiết đơn hàng
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, Authentication auth) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Kiểm tra đơn hàng có phải của user này không
        if (!order.getUser().getEmail().equals(auth.getName())) {
            return "redirect:/orders";
        }

        model.addAttribute("order", order);
        model.addAttribute("orders", List.of(order));
        model.addAttribute("cartCount", cartService.getCartItemCount(auth.getName()));
        return "user/order-history";
    }
}
