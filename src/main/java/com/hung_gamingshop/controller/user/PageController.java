package com.hung_gamingshop.controller.user;

import com.hung_gamingshop.service.CartService;
import com.hung_gamingshop.service.ContactMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PageController {

    @Autowired private CartService cartService;
    @Autowired private ContactMessageService contactMessageService;

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

    @PostMapping("/contact")
    public String sendContact(@RequestParam String name,
                              @RequestParam String email,
                              @RequestParam(required = false) String phone,
                              @RequestParam String message,
                              RedirectAttributes redirectAttributes) {
        contactMessageService.create(name, email, phone, message);
        redirectAttributes.addFlashAttribute("message", "Đã gửi yêu cầu tư vấn. Admin sẽ đọc và phản hồi sớm nhất.");
        return "redirect:/contact";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403";
    }
}
