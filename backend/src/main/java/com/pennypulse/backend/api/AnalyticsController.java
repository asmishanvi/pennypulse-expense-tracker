package com.pennypulse.backend.api;

import com.pennypulse.backend.service.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public AnalyticsService.Summary summary(@RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return analyticsService.summary(month);
    }

    @GetMapping("/category")
    public java.util.List<AnalyticsService.CategoryTotal> category(@RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return analyticsService.categoryTotals(month);
    }

    @GetMapping("/weekly")
    public java.util.List<AnalyticsService.WeeklyTotal> weekly(@RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return analyticsService.weeklyTotals(month);
    }
}
