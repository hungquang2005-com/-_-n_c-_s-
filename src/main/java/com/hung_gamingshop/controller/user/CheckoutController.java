package com.hung_gamingshop.controller.user;

import com.hung_gamingshop.model.Order;
import com.hung_gamingshop.model.User;
import com.hung_gamingshop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired private CartService cartService;
    @Autowired private OrderService orderService;
    @Autowired private PaymentService paymentService;
    @Autowired private UserService userService;

    // Bước 1: Nhập thông tin thanh toán
    @GetMapping("/info")
    public String infoPage(Model model, Authentication auth) {
        var cart = cartService.getOrCreateCart(auth.getName());
        if (cart.getItems().isEmpty()) return "redirect:/cart";

        // Điền sẵn thông tin user
        User user = userService.findByEmail(auth.getName()).orElse(null);
        model.addAttribute("cart", cart);
        model.addAttribute("user", user);
        model.addAttribute("cartCount", cartService.getCartItemCount(auth.getName()));
        return "user/info";
    }

    // Bước 2: Chọn phương thức thanh toán
    @PostMapping("/payment")
    public String paymentPage(@RequestParam String fullName,
                              @RequestParam String email,
                              @RequestParam String phone,
                              @RequestParam String address,
                              @RequestParam(required = false) String note,
                              Model model, Authentication auth) {
        var cart = cartService.getOrCreateCart(auth.getName());
        if (cart.getItems().isEmpty()) return "redirect:/cart";

        model.addAttribute("cart", cart);
        model.addAttribute("fullName", fullName);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("address", address);
        model.addAttribute("note", note);
        model.addAttribute("cartCount", cartService.getCartItemCount(auth.getName()));
        return "user/payment";
    }

    // Bước 3: Xem hóa đơn xác nhận
    @PostMapping("/invoice")
    public String invoicePage(@RequestParam String fullName,
                              @RequestParam String email,
                              @RequestParam String phone,
                              @RequestParam String address,
                              @RequestParam(required = false) String note,
                              @RequestParam String paymentMethod,
                              Model model, Authentication auth) {
        var cart = cartService.getOrCreateCart(auth.getName());
        if (cart.getItems().isEmpty()) return "redirect:/cart";

        model.addAttribute("cart", cart);
        model.addAttribute("fullName", fullName);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("address", address);
        model.addAttribute("note", note);
        model.addAttribute("paymentMethod", paymentMethod);
        model.addAttribute("cartCount", cartService.getCartItemCount(auth.getName()));
        return "user/invoice";
    }

    // Bước 4: Xác nhận đặt hàng → tạo order
    @PostMapping("/confirm")
    public String confirm(@RequestParam String fullName,
                          @RequestParam String email,
                          @RequestParam String phone,
                          @RequestParam String address,
                          @RequestParam(required = false) String note,
                          @RequestParam String paymentMethod,
                          Authentication auth,
                          RedirectAttributes redirectAttributes) {
        try {
            Order.PaymentMethod method = Order.PaymentMethod.valueOf(paymentMethod);
            Order order = orderService.createOrder(
                    auth.getName(), fullName, email, phone, address, note, method);
            paymentService.createPayment(order);
            return "redirect:/checkout/success/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    // Bước 5: Trang thành công
    @GetMapping("/success/{orderId}")
    public String successPage(@PathVariable Long orderId, Model model, Authentication auth) {
        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        model.addAttribute("order", order);
        model.addAttribute("cartCount", 0);
        return "user/success";
    }
}
