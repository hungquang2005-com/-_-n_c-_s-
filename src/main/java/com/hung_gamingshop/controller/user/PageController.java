package com.hung_gamingshop.controller.user;

import com.hung_gamingshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @Autowired private CartService cartService;

    @GetMapping("/about")
    public String about(Model model, Authentication auth) {
        if (auth != null) model.addAttribute("cartCount", cartService.getCartItemCount(auth.getName()));
        return "user/about";
    }

    @GetMapping("/contact")
    public String contact(Model model, Authentication auth) {
        if (auth != null) model.addAttribute("cartCount", cartService.getCartItemCount(auth.getName()));
        return "user/contact";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403";
    }
}