package com.hung_gamingshop.controller.admin;

import com.hung_gamingshop.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private DashboardService dashboardService;

    // Dashboard chính
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        Map<String, Object> stats = dashboardService.getDashboardStats();
        model.addAllAttributes(stats);
        return "admin/admin-dashboard";
    }
}