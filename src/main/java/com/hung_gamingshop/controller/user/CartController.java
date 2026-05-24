package com.hung_gamingshop.controller.user;

import com.hung_gamingshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired private CartService cartService;

    // Xem giỏ hàng
    @GetMapping
    public String viewCart(Model model, Authentication auth) {
        model.addAttribute("cart", cartService.getOrCreateCart(auth.getName()));
        model.addAttribute("cartCount", cartService.getCartItemCount(auth.getName()));
        return "user/history";
    }

    // Thêm vào giỏ hàng
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            Authentication auth,
                            RedirectAttributes redirectAttributes) {
        try {
            cartService.addToCart(auth.getName(), productId, quantity);
            redirectAttributes.addFlashAttribute("message", "Đã thêm vào giỏ hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products/" + productId;
    }

    // Cập nhật số lượng
    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long itemId,
                                 @RequestParam int quantity,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        try {
            cartService.updateQuantity(auth.getName(), itemId, quantity);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    // Xóa sản phẩm khỏi giỏ
    @PostMapping("/remove")
    public String removeItem(@RequestParam Long itemId,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {
        try {
            cartService.removeItem(auth.getName(), itemId);
            redirectAttributes.addFlashAttribute("message", "Đã xóa sản phẩm khỏi giỏ hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }
}
