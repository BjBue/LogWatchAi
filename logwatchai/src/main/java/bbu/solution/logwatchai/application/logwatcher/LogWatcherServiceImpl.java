package bbu.solution.logwatchai.application.logwatcher;

import bbu.solution.logwatchai.domain.log.LogEntry;
import bbu.solution.logwatchai.domain.log.LogEntryService;
import bbu.solution.logwatchai.domain.logsource.LogSource;
import bbu.solution.logwatchai.domain.logsource.LogSourceService;
import bbu.solution.logwatchai.domain.logwatcher.LogEvent;
import bbu.solution.logwatchai.domain.logwatcher.LogWatcherService;
import bbu.solution.logwatchai.domain.appconfig.AppConfigService;
import bbu.solution.logwatchai.domain.appconfig.AppConfig;
import bbu.solution.logwatchai.domain.analysis.AIAnalysisService;
import bbu.solution.logwatchai.infrastructure.logwatcher.DirectoryWatcher;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class LogWatcherServiceImpl implements LogWatcherService {

    private final AppConfig config;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final LogEntryService logEntryService;
    private final Map<String, UUID> fileSourceMap = new HashMap<>();
    private final LogSourceService logSourceService;

    @Autowired
    private LogWatcherService self;
    @Autowired
    private AIAnalysisService aIAnalysisService;

    public LogWatcherServiceImpl(
            AppConfigService configService,
            LogEntryService logEntryService,
            LogSourceService logSourceService
    ) {
        this.config = configService.getConfig();
        this.logEntryService = logEntryService;
        this.logSourceService = logSourceService;
    }

    @PostConstruct
    @Override
    public void startWatching() {
        System.out.println("Starting LogWatcher...");

        // 1) Erzeuge / hole LogSources
        for (String path : config.getWatchPaths()) {
            LogSource src = logSourceService.findByPath(path)
                    .orElseGet(() -> logSourceService.createSource(path));
            fileSourceMap.put(path, src.getId());
        }

        Map<Path, List<Path>> grouped = groupByDirectory(config.getWatchPaths());

        grouped.forEach((dir, files) -> {
            executor.submit(() -> {
                try {
                    DirectoryWatcher watcher = new DirectoryWatcher(dir, self::handleEvent);

                    for (Path file : files) {
                        watcher.addFile(file);
                        System.out.println("Watching file: " + file);
                    }

                    watcher.run();
                } catch (Exception e) {
                    System.err.println("Failed to watch dir " + dir + ": " + e.getMessage());
                }
            });
        });
    }

    private Map<Path, List<Path>> groupByDirectory(List<String> paths) {
        Map<Path, List<Path>> map = new HashMap<>();

        paths.stream()
                .map(Paths::get)
                .forEach(path -> map
                        .computeIfAbsent(path.getParent(), k -> new ArrayList<>())
                        .add(path)
                );

        return map;
    }

    @Override
    @Transactional
    public void handleEvent(LogEvent event) {

        UUID sourceId = fileSourceMap.get(event.getFilePath());
        if (sourceId == null) {
            System.err.println("No LogSource for " + event.getFilePath());
            return;
        }

        // todo: normalize path map lookup (optional but recommended)
        // String normalizedPath = Paths.get(event.getFilePath()).toAbsolutePath().normalize().toString();
        // UUID sourceId = fileSourceMap.get(normalizedPath);

        // Try to save atomically (saveRawLog handles duplicates via DB constraint)
        LogEntry entry = logEntryService.saveRawLog(event.getLine(), sourceId);

        // trigger async analysis (saveRawLog returns existing entry if duplicate)
        logEntryService.analyzeAsync(entry);
    }
}
