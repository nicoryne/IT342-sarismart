package me.sarismart.backend.Entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    private String reportType;
    private String period;
    private double totalSales;
    private int totalTransactions;
}
