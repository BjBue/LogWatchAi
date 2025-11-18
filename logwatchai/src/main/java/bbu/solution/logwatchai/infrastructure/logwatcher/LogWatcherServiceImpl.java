package bbu.solution.logwatchai.infrastructure.logwatcher;

import bbu.solution.logwatchai.domain.logwatcher.LogEvent;
import bbu.solution.logwatchai.domain.logwatcher.LogWatcherService;
import bbu.solution.logwatchai.domain.appconfig.AppConfigService;
import bbu.solution.logwatchai.infrastructure.appconfig.AppConfig;
import jakarta.annotation.PostConstruct;
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

    public LogWatcherServiceImpl(AppConfigService configService) {
        this.config = configService.getConfig();
    }

    @PostConstruct
    @Override
    public void startWatching() {
        System.out.println("Starting LogWatcher...");

        Map<Path, List<Path>> grouped = groupByDirectory(config.getWatchPaths());

        grouped.forEach((dir, files) -> {
            executor.submit(() -> {
                try {
                    DirectoryWatcher watcher = new DirectoryWatcher(dir, this::handleEvent);

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

    private void handleEvent(LogEvent event) {
        System.out.println("NEW LOG EVENT: " + event.getLine());
        // später: Alert, AI, Mail, Persistenz
    }
}
