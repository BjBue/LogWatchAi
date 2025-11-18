package bbu.solution.logwatchai.domain.source;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LogSourceRepository extends JpaRepository<LogSource, UUID> {
    List<LogSource> findByActiveTrue();
    List<LogSource> findByType(SourceType type);
}