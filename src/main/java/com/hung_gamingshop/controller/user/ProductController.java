package com.hung_gamingshop.controller.user;

import com.hung_gamingshop.exception.ResourceNotFoundException;
import com.hung_gamingshop.service.CartService;
import com.hung_gamingshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired private ProductService productService;
    @Autowired private CartService cartService;

    // Danh sách sản phẩm + tìm kiếm + lọc category
    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String category,
                       Model model, Authentication auth) {
        if (keyword != null && !keyword.isBlank()) {
            model.addAttribute("products", productService.searchProducts(keyword));
            model.addAttribute("keyword", keyword);
        } else if (category != null && !category.isBlank()) {
            model.addAttribute("products", productService.getByCategory(category));
            model.addAttribute("selectedCategory", category);
        } else {
            model.addAttribute("products", productService.getAllProducts());
        }
        model.addAttribute("categories", productService.getAllCategories());
        if (auth != null && auth.isAuthenticated()) model.addAttribute("cartCount", cartService.getCartItemCount(auth.getName()));
        return "user/list";
    }

    // Chi tiết sản phẩm
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, Authentication auth) {
        model.addAttribute("product",
                productService.getById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm!")));
        if (auth != null && auth.isAuthenticated()) model.addAttribute("cartCount", cartService.getCartItemCount(auth.getName()));
        return "user/detail";
    }
}
