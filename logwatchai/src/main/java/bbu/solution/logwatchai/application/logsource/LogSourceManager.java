package bbu.solution.logwatchai.application.logsource;

import bbu.solution.logwatchai.domain.logsource.LogSource;
import bbu.solution.logwatchai.domain.logsource.LogSourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogSourceManager {

    private final LogSourceService logSourceService;
    private final LogSourceWorkers workers;

    @EventListener(ApplicationReadyEvent.class)
    public void startAllSources() {
        log.info("LogSourceManager startet – load active LogSources ...");

        List<LogSource> sources = logSourceService.getActiveSources();

        for (LogSource source : sources) {
            log.info("Start LogSource '{}', Typ={}", source.getName(), source.getType());
            workers.startWorkerFor(source);
        }
    }
}
