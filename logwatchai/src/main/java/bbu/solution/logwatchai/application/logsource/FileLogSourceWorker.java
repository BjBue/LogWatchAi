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

/**
 * FileLogSourceWorker monitors local file-based log sources and triggers ingestion
 * whenever the observed log file is modified. It uses a dedicated thread pool and a
 * WatchService-based file system observer to detect updates in near real time.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileLogSourceWorker {

    private final LogEntryService logEntryService;

    /**
     * Executor service for running dedicated watcher threads per file source.
     * Threads are created with a descriptive prefix for easier debugging.
     */
    private final ExecutorService executor =
            Executors.newCachedThreadPool(new CustomizableThreadFactory("file-logsource-"));

    /**
     * Starts monitoring the file defined by the given LogSource.
     * A separate thread is spawned to run the watch loop.
     *
     * @param source the log source describing the file to monitor
     */
    public void start(LogSource source) {
        executor.submit(() -> {
            try {
                watchFile(source);
            } catch (Exception e) {
                log.error("Error in file-watcher for source {}", source.getName(), e);
            }
        });
    }

    /**
     * Watches the file associated with the provided LogSource for modification events and
     * triggers ingestion whenever the file is updated.
     *
     * <p><b>Detailed Internal Workflow (requested special documentation):</b></p>
     * <ol>
     *     <li>I resolve the file path from the LogSource.</li>
     *     <li>I create a WatchService to monitor file system events.</li>
     *     <li>I register the parent directory of the file with interest in ENTRY_MODIFY events.
     *         <ul>
     *            <li>The WatchService cannot watch a file directly — only directories.</li>
     *            <li>Therefore, I check for modifications to the file within the directory.</li>
     *         </ul>
     *     </li>
     *     <li>I enter an infinite loop where I:
     *         <ul>
     *             <li>Block on {@code watchService.take()} until an event occurs.</li>
     *             <li>Handle interruption cleanly and terminate if necessary.</li>
     *         </ul>
     *     </li>
     *     <li>For each file system event:
     *         <ul>
     *             <li>I check whether the modified file is the one we monitor.</li>
     *             <li>If yes, I call {@code logEntryService.ingestFileUpdate(...)}.</li>
     *         </ul>
     *     </li>
     *     <li>If the watch key becomes invalid (e.g., directory removed), I stop watching.</li>
     * </ol>
     *
     * <p>This method runs indefinitely until:</p>
     * <ul>
     *     <li>the thread is interrupted, or</li>
     *     <li>the watch key becomes invalid (e.g., directory deleted), or</li>
     *     <li>an unrecoverable IO error occurs.</li>
     * </ul>
     *
     * @param source the log source containing metadata and the file path to watch
     * @throws IOException if the WatchService cannot be created or the file cannot be accessed
     */
    private void watchFile(LogSource source) throws IOException {
        Path filePath = Paths.get(source.getPath());
        log.info("FileWatcher started for {}", filePath);

        // Create a WatchService for filesystem events
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            Path dir = filePath.getParent();
            if (dir == null) {
                log.error("Filepath not valid: {}", filePath);
                return;
            }

            // Register the directory to watch for modifications
            dir.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY
            );

            while (true) {
                WatchKey key;
                try {
                    // I block until a filesystem event occurs
                    key = watchService.take();
                } catch (InterruptedException e) {
                    log.warn("File watcher interrupted for {}", source.getName());
                    Thread.currentThread().interrupt();
                    return; // stops the worker
                }

                // Process all events for this key
                for (WatchEvent<?> event : key.pollEvents()) {
                    Path changed = (Path) event.context();

                    // I check whether the event relates to the file we monitor
                    if (changed.endsWith(filePath.getFileName())) {
                        log.info("Change detected in {}", filePath);
                        logEntryService.ingestFileUpdate(source, filePath);
                    }
                }

                // If the key cannot be reset, watching must stop
                if (!key.reset()) {
                    break;
                }
            }
        }
    }
}
