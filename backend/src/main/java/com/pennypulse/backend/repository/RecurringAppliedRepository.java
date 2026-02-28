package com.pennypulse.backend.repository;

import com.pennypulse.backend.domain.RecurringApplied;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.Optional;

public interface RecurringAppliedRepository extends JpaRepository<RecurringApplied, Long> {
    Optional<RecurringApplied> findByRecurringIdAndMonth(Long recurringId, YearMonth month);
}
