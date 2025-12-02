package bbu.solution.logwatchai.application.logwatcher;

import bbu.solution.logwatchai.domain.log.LogEntry;
import bbu.solution.logwatchai.domain.log.LogEntryService;
import bbu.solution.logwatchai.domain.logsource.LogSource;
import bbu.solution.logwatchai.domain.logsource.LogSourceService;
import bbu.solution.logwatchai.domain.logwatcher.LogEvent;
import bbu.solution.logwatchai.domain.logwatcher.LogWatcherService;
import bbu.solution.logwatchai.domain.appconfig.AppConfigService;
import bbu.solution.logwatchai.domain.appconfig.AppConfig;
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

/**
 * Service implementation that watches configured filesystem paths for log updates,
 * ensures corresponding LogSource entities exist, and delegates ingestion and analysis
 * of new log lines.
 *
 * <p>I start directory watchers for configured watch paths, map files to their LogSource IDs,
 * and forward new log lines into the persistence and analysis pipeline.</p>
 */
@Service
public class LogWatcherServiceImpl implements LogWatcherService {

    private final AppConfig config;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final LogEntryService logEntryService;
    private final Map<String, UUID> fileSourceMap = new HashMap<>();
    private final LogSourceService logSourceService;

    @Autowired
    private LogWatcherService self;

    /**
     * Constructs the LogWatcherServiceImpl and initializes required services.
     *
     * <p>I obtain the application configuration via the provided AppConfigService and
     * store references to services used for persisting and analyzing log entries.</p>
     *
     * @param configService   service providing application configuration (watch paths)
     * @param logEntryService service for saving and analyzing log entries
     * @param logSourceService service for creating and resolving LogSource entities
     */
    public LogWatcherServiceImpl(
            AppConfigService configService,
            LogEntryService logEntryService,
            LogSourceService logSourceService
    ) {
        this.config = configService.getConfig();
        this.logEntryService = logEntryService;
        this.logSourceService = logSourceService;
    }

    /**
     * Initializes watching of configured file paths after application startup.
     *
     * <p>I perform the following actions when the application is ready:</p>
     * <ol>
     *   <li>I iterate over all configured watch paths from the application config.</li>
     *   <li>For each path I either find an existing {@link LogSource} by path or create a new one,
     *       and I store the mapping from file path to the source UUID in {@code fileSourceMap}.</li>
     *   <li>I group the configured file paths by their parent directory so I can create a single
     *       directory watcher per directory.</li>
     *   <li>For each directory group I submit a task to the executor which:
     *       <ol>
     *         <li>Creates a {@link DirectoryWatcher} for the directory and registers files to watch.</li>
     *         <li>Logs which files are being watched.</li>
     *         <li>Starts the watch loop by calling {@code watcher.run()} (blocking call inside the task).</li>
     *       </ol>
     *   </li>
     *   <li>If any watcher task fails to start, I log the failure to stderr.</li>
     * </ol>
     *
     * <p>NOTE: This method is annotated with {@link PostConstruct} so it runs once the Spring
     * context is initialized.</p>
     */
    @PostConstruct
    @Override
    public void startWatching() {
        System.out.println("Starting LogWatcher...");

        for (String path : config.getWatchPaths()) { // Loop over all configured watch paths (each path is a String)
            LogSource src = logSourceService.findByPath(path)   // Try to find an existing LogSource for this path
                    .orElseGet(() -> logSourceService.createSource(path)); // If none exists, create a new LogSource for this path
            fileSourceMap.put(path, src.getId()); // Store a mapping from the path to the LogSource ID in fileSourceMap
        }

        Map<Path, List<Path>> grouped = groupByDirectory(config.getWatchPaths()); // Group all watch paths by their parent directory; result: directory -> list of files

        grouped.forEach((dir, files) -> { // For each directory and its associated list of files
            executor.submit(() -> { // Submit a new asynchronous task to the executor for this directory
                try {
                    DirectoryWatcher watcher = new DirectoryWatcher(dir, self::handleEvent); // Create a DirectoryWatcher for this directory, using handleEvent as callback

                    for (Path file : files) { // Loop over all files that belong to this directory
                        watcher.addFile(file); // Tell the watcher to observe this specific file
                        System.out.println("Watching file: " + file); // Log to the console which file is being watched
                    }

                    watcher.run(); // Start the watcher; this will block and listen for file system events
                } catch (Exception e) { // Catch any exception that occurs in this task
                    System.err.println("Failed to watch dir " + dir + ": " + e.getMessage()); // Log an error to stderr if the directory cannot be watched
                }
            });
        });
    }

    /**
     * Groups a list of string file paths by their parent directory.
     *
     * <p>I convert each configured path to a {@link Path} instance, determine its parent directory,
     * and build a mapping from parent directory to the list of files contained in that directory.
     * This enables me to create one directory watcher per directory instead of one per file.</p>
     *
     * @param paths list of file path strings to group
     * @return a map where keys are parent directories and values are lists of file paths in those directories
     */
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

    /**
     * Handles an incoming {@link LogEvent} produced by a directory watcher.
     *
     * <p>I perform the following actions when a new log line event arrives:</p>
     * <ol>
     *   <li>I look up the {@link UUID} of the {@link LogSource} associated with the file path
     *       present in the event using {@code fileSourceMap}.</li>
     *   <li>If no LogSource is found, I write an error message to stderr and abort processing for this event.</li>
     *   <li>Otherwise, I call {@link LogEntryService#saveRawLog(String, UUID)} to persist the raw log line.
     *       That method uses an insert-ignore-duplicate strategy so duplicates are handled atomically.</li>
     *   <li>Finally, I trigger asynchronous analysis for the saved entry by calling {@link LogEntryService#analyzeAsync(LogEntry)}.</li>
     * </ol>
     *
     * <p>I keep this method transactional to ensure that any DB operations triggered directly here are executed
     * within a transaction.</p>
     *
     * @param event the log event containing the file path and the new log line
     */
    @Override
    @Transactional
    public void handleEvent(LogEvent event) {
        UUID sourceId = fileSourceMap.get(event.getFilePath());
        if (sourceId == null) {
            System.err.println("No LogSource for " + event.getFilePath());
            return;
        }
        // Try to save atomically (saveRawLog handles duplicates via DB constraint)
        LogEntry entry = logEntryService.saveRawLog(event.getLine(), sourceId);
        // trigger async analysis (saveRawLog returns existing entry if duplicate)
        logEntryService.analyzeAsync(entry);
    }
}
