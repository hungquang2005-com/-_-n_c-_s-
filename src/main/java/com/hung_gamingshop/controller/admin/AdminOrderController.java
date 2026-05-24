package com.hung_gamingshop.controller.admin;

import com.hung_gamingshop.exception.ResourceNotFoundException;
import com.hung_gamingshop.model.Order;
import com.hung_gamingshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired private OrderService orderService;

    // Danh sách tất cả đơn hàng
    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("statuses", Order.OrderStatus.values());
        return "admin/order-list";
    }

    // Cập nhật trạng thái đơn hàng
    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status,
                               RedirectAttributes redirectAttributes) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
            orderService.updateStatus(id, orderStatus);
            redirectAttributes.addFlashAttribute("message",
                    "Cập nhật trạng thái đơn hàng #" + id + " thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/orders";
    }
}
