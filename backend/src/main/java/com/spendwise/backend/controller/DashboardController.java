package com.spendwise.backend.controller;

import com.spendwise.backend.dto.DashboardResponse;
import com.spendwise.backend.service.DashboardService;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {
    private final DashboardService dashboardService;

        public DashboardController(DashboardService dashboardService) {
            this.dashboardService = dashboardService;
        }

        @GetMapping("/dashboard")
        public DashboardResponse getDashboard(
                @RequestParam YearMonth month) {

            return dashboardService.getDashboard(month);
        }
}
