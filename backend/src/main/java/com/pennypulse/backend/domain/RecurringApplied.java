package com.pennypulse.backend.domain;

import jakarta.persistence.*;
import java.time.YearMonth;

@Entity
@Table(name = "recurring_applied", uniqueConstraints = @UniqueConstraint(columnNames = {"recurring_id", "month"}))
public class RecurringApplied {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recurring_id", nullable = false)
    private Long recurringId;

    @Column(length = 7, nullable = false)
    private YearMonth month;

    public Long getId() { return id; }
    public Long getRecurringId() { return recurringId; }
    public void setRecurringId(Long recurringId) { this.recurringId = recurringId; }
    public YearMonth getMonth() { return month; }
    public void setMonth(YearMonth month) { this.month = month; }
}
