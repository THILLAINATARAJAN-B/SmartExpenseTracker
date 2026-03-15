package com.smartexpense.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {

    private BigDecimal totalSpentThisMonth;
    private Map<String, BigDecimal> spentByCategory;
    private Map<String, BigDecimal> budgetByCategory;
    private Map<String, String> budgetAlerts;  // category -> "EXCEEDED" or "OK"
}
