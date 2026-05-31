package com.hung_gamingshop.controller.admin;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/auth")
public class AdminAuthController {

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Authentication auth,
                            Model model) {

        // Đã đăng nhập với ROLE_ADMIN → vào thẳng dashboard
        if (auth != null && auth.isAuthenticated()
                && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }

        if (error != null) {
            model.addAttribute("error", "Tài khoản hoặc mật khẩu không đúng, hoặc bạn không có quyền Admin!");
        }
        if (logout != null) {
            model.addAttribute("message", "Đăng xuất thành công!");
        }

        return "admin/login";
    }
}