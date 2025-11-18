package bbu.solution.logwatchai.domain.report;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface DailyReportService {
    DailyReport generateForDate(LocalDate date);
    void markDelivered(UUID reportId);
    Optional<DailyReport> getByDate(LocalDate date);
}