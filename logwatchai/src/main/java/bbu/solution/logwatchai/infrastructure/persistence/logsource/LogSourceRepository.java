package bbu.solution.logwatchai.infrastructure.persistence.logsource;

import bbu.solution.logwatchai.domain.logsource.LogSource;
import bbu.solution.logwatchai.domain.logsource.LogSourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LogSourceRepository extends JpaRepository<LogSource, UUID> {
    List<LogSource> findByActiveTrue();
    List<LogSource> findByType(LogSourceType type);
}