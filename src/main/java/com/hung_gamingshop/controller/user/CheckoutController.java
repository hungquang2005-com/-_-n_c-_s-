package com.hung_gamingshop.controller.user;

import com.hung_gamingshop.model.Order;
import com.hung_gamingshop.model.User;
import com.hung_gamingshop.service.CartService;
import com.hung_gamingshop.service.OrderService;
import com.hung_gamingshop.service.PaymentService;
import com.hung_gamingshop.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    // =========================
    // BƯỚC 1: NHẬP THÔNG TIN
    // =========================
    @GetMapping("/info")
    public String infoPage(Model model, Authentication auth) {

        var cart = cartService.getOrCreateCart(auth.getName());

        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        User user = userService.findByEmail(auth.getName()).orElse(null);

        model.addAttribute("cart", cart);
        model.addAttribute("user", user);

        model.addAttribute("cartCount",
                cartService.getCartItemCount(auth.getName()));

        return "user/info";
    }

    // =========================
    // BƯỚC 2: POST THÔNG TIN
    // =========================
    @PostMapping("/payment")
    public String paymentPage(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam(required = false) String note,
            HttpSession session
    ) {

        session.setAttribute("fullName", fullName);
        session.setAttribute("email", email);
        session.setAttribute("phone", phone);
        session.setAttribute("address", address);
        session.setAttribute("note", note);

        return "redirect:/checkout/payment";
    }

    // =========================
    // BƯỚC 2: GET PAYMENT
    // =========================
    @GetMapping("/payment")
    public String paymentGet(
            Model model,
            Authentication auth,
            HttpSession session
    ) {

        var cart = cartService.getOrCreateCart(auth.getName());

        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("cart", cart);

        model.addAttribute("fullName",
                session.getAttribute("fullName"));

        model.addAttribute("email",
                session.getAttribute("email"));

        model.addAttribute("phone",
                session.getAttribute("phone"));

        model.addAttribute("address",
                session.getAttribute("address"));

        model.addAttribute("note",
                session.getAttribute("note"));

        model.addAttribute("cartCount",
                cartService.getCartItemCount(auth.getName()));

        return "user/payment";
    }

    // =========================
    // BƯỚC 3: POST INVOICE
    // =========================
    @PostMapping("/invoice")
    public String invoicePage(
            @RequestParam String paymentMethod,
            HttpSession session
    ) {

        session.setAttribute("paymentMethod", paymentMethod);

        return "redirect:/checkout/invoice";
    }

    // =========================
    // BƯỚC 3: GET INVOICE
    // =========================
    @GetMapping("/invoice")
    public String invoiceGet(
            Model model,
            Authentication auth,
            HttpSession session
    ) {

        var cart = cartService.getOrCreateCart(auth.getName());

        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("cart", cart);

        model.addAttribute("fullName",
                session.getAttribute("fullName"));

        model.addAttribute("email",
                session.getAttribute("email"));

        model.addAttribute("phone",
                session.getAttribute("phone"));

        model.addAttribute("address",
                session.getAttribute("address"));

        model.addAttribute("note",
                session.getAttribute("note"));

        model.addAttribute("paymentMethod",
                session.getAttribute("paymentMethod"));

        model.addAttribute("cartCount",
                cartService.getCartItemCount(auth.getName()));

        return "user/invoice";
    }

    // =========================
    // BƯỚC 4: XÁC NHẬN ĐẶT HÀNG
    // =========================
    @PostMapping("/confirm")
    public String confirm(
            Authentication auth,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {

            String fullName =
                    (String) session.getAttribute("fullName");

            String email =
                    (String) session.getAttribute("email");

            String phone =
                    (String) session.getAttribute("phone");

            String address =
                    (String) session.getAttribute("address");

            String note =
                    (String) session.getAttribute("note");

            String paymentMethod =
                    (String) session.getAttribute("paymentMethod");

            Order.PaymentMethod method =
                    Order.PaymentMethod.valueOf(paymentMethod);

            Order order = orderService.createOrder(
                    auth.getName(),
                    fullName,
                    email,
                    phone,
                    address,
                    note,
                    method
            );

            paymentService.createPayment(order);

            // clear session checkout
            session.removeAttribute("fullName");
            session.removeAttribute("email");
            session.removeAttribute("phone");
            session.removeAttribute("address");
            session.removeAttribute("note");
            session.removeAttribute("paymentMethod");

            return "redirect:/checkout/success/" + order.getId();

        } catch (Exception e) {

            e.printStackTrace();

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );

            return "redirect:/cart";
        }
    }

    // =========================
    // BƯỚC 5: THÀNH CÔNG
    // =========================
    @GetMapping("/success/{orderId}")
    public String successPage(
            @PathVariable Long orderId,
            Model model,
            Authentication auth) {

        Order order = orderService.getOrderById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy đơn hàng"));

        model.addAttribute("order", order);

        model.addAttribute("cartCount", 0);

        return "user/success";
    }
}