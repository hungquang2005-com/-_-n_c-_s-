package com.hung_gamingshop.controller.user;

import com.hung_gamingshop.service.CartService;
import com.hung_gamingshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired private ProductService productService;
    @Autowired private CartService cartService;

    @GetMapping({"/", "/home"})
    public String home(Model model, Authentication auth) {
        model.addAttribute("featuredProducts", productService.getFeaturedProducts());
        model.addAttribute("categories", productService.getAllCategories());
        if (auth != null && auth.isAuthenticated()) {
            model.addAttribute("cartCount", cartService.getCartItemCount(auth.getName()));
        }
        return "user/index";
    }
}
