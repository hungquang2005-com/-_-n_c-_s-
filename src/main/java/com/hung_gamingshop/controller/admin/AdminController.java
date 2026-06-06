package com.hung_gamingshop.controller.admin;

import com.hung_gamingshop.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
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

    // Trang doanh thu
    @GetMapping("/revenue")
    public String revenue(
            @RequestParam(defaultValue = "day")   String viewType,
            @RequestParam(required = false)        Integer year,
            @RequestParam(required = false)        Integer month,
            Model model) {

        int currentYear  = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        if (year  == null) year  = currentYear;
        if (month == null) month = currentMonth;

        // Clamp month về 1–12
        if (month < 1)  month = 1;
        if (month > 12) month = 12;

        Map<String, Object> stats =
                dashboardService.getRevenuePageStats(viewType, year, month);
        model.addAllAttributes(stats);
        return "admin/admin-revenue";
    }
}