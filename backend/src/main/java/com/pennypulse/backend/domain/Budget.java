package com.pennypulse.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.YearMonth;

@Entity
@Table(name = "budgets")
public class Budget {
    @Id
    @Column(length = 7)
    private YearMonth month;

    @NotNull
    private Double amount;

    public YearMonth getMonth() { return month; }
    public void setMonth(YearMonth month) { this.month = month; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
