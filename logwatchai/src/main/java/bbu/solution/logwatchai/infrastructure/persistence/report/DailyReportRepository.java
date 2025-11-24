package bbu.solution.logwatchai.infrastructure.persistence.report;

import bbu.solution.logwatchai.domain.report.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DailyReportRepository extends JpaRepository<DailyReport, UUID> {
    Optional<DailyReport> findByReportedDate(LocalDate date);
    List<DailyReport> findByDeliveredFalse();
}