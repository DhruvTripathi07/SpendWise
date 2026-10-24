package com.spendwise.backend.controller;

import com.spendwise.backend.dto.InsightResponse;
import com.spendwise.backend.service.InsightService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/insights")
public class InsightController {

    private final InsightService insightService;

    public InsightController(InsightService insightService) {
        this.insightService = insightService;
    }

    @GetMapping
    public List<InsightResponse> getInsights(
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM")
            YearMonth month) {

        return insightService.generateInsights(month);
    }
}
