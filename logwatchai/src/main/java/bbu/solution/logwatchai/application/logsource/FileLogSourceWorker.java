package bbu.solution.logwatchai.application.logsource;

import bbu.solution.logwatchai.domain.log.LogEntryService;
import bbu.solution.logwatchai.domain.logsource.LogSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileLogSourceWorker {

    private final LogEntryService logEntryService;

    private final ExecutorService executor =
            Executors.newCachedThreadPool(new CustomizableThreadFactory("file-logsource-"));

    public void start(LogSource source) {
        executor.submit(() -> {
            try {
                watchFile(source);
            } catch (Exception e) {
                log.error("Error in file-watcher for source {}", source.getName(), e);
            }
        });
    }

    private void watchFile(LogSource source) throws IOException{
        Path filePath = Paths.get(source.getPath());
        log.info("FileWatcher started for {}", filePath);

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            Path dir = filePath.getParent();
            if (dir == null) {
                log.error("Filepath not valid: {}", filePath);
                return;
            }

            dir.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY
            );

            while (true) {
                WatchKey key;
                try {
                    key = watchService.take();
                }catch (InterruptedException e){
                    log.warn("File watcher interrupted for {}", source.getName());
                    Thread.currentThread().interrupt();
                    return; //stopps the worker
                }
                for (WatchEvent<?> event : key.pollEvents()) {
                    Path changed = (Path) event.context();
                    if (changed.endsWith(filePath.getFileName())) {
                        log.info("Change in in {}", filePath);
                        logEntryService.ingestFileUpdate(source, filePath);
                    }
                }

                if(!key.reset()) {
                    break;
                }
            }
        }
    }
}
