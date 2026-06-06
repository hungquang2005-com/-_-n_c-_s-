package com.hung_gamingshop.controller.admin;

import com.hung_gamingshop.model.User;
import com.hung_gamingshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("totalUsers", users.size());
        return "admin/user-list";
    }
}