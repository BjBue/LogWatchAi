package bbu.solution.logwatchai.infrastructure.logwatcher;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Watches a specific directory for file modification events and triggers a corresponding
 * {@link TailReader} to read newly appended lines from watched files.
 * <p>
 * I use Java's {@link WatchService} to listen for {@code ENTRY_MODIFY} events. When a file
 * that I monitor is modified, I request the associated {@link TailReader} to read and
 * forward newly added lines through the provided {@link TailReader.LineHandler}.
 */
public class DirectoryWatcher implements Runnable {

    /**
     * The Java NIO watch service that allows me to receive file system events.
     */
    private final WatchService watchService;

    /**
     * Keeps track of files and their corresponding {@link TailReader} instances.
     * I use a {@link ConcurrentHashMap} to ensure thread-safe access.
     */
    private final Map<Path, TailReader> readers = new ConcurrentHashMap<>();

    /**
     * The callback handler that receives newly read lines from any tail reader.
     */
    private final TailReader.LineHandler handler;

    /**
     * Creates a new DirectoryWatcher for the given directory.
     * <p>
     * I register the directory with the {@link WatchService} so that I only receive
     * {@code ENTRY_MODIFY} events.
     *
     * @param directory The directory that I should watch.
     * @param handler   The handler to receive new lines read from files.
     * @throws IOException if the watch service cannot be created or registration fails.
     */
    public DirectoryWatcher(Path directory, TailReader.LineHandler handler) throws IOException {
        // I create a watch service used to listen for file events
        this.watchService = FileSystems.getDefault().newWatchService();
        this.handler = handler;

        // I register the given directory for modify events
        directory.register(watchService, ENTRY_MODIFY);
    }

    /**
     * Adds a file to the list of monitored files.
     * <p>
     * I create a {@link TailReader} for the file, initialize it so that it
     * knows where the current end of the file is, and store it for future modify events.
     *
     * @param file The file that I will watch for tail updates.
     * @throws IOException if initialization of the {@link TailReader} fails.
     */
    public void addFile(Path file) throws IOException {
        // I create a tail reader for this file
        TailReader reader = new TailReader(file);

        // I initialize the reader so it positions itself at the file's end
        reader.initialize();

        // I store the reader for lookup during modify events
        readers.put(file, reader);
    }

    /**
     * Continuously waits for and processes file modify events.
     * <p>
     * I block until a watch key is available, then for each modify event,
     * I determine the affected file. If I monitor that file, I instruct
     * the corresponding {@link TailReader} to read and forward new lines.
     */
    @Override
    public void run() {
        try {
            // I loop indefinitely to continuously process file events
            while (true) {
                // I wait for the next event key (blocks until available)
                WatchKey key = watchService.take();

                // I retrieve the directory being watched
                Path dir = (Path) key.watchable();

                // I iterate through all events triggered for this key
                for (WatchEvent<?> event : key.pollEvents()) {
                    // I only react to modify events
                    if (event.kind() == ENTRY_MODIFY) {
                        // I resolve the filename from the event to a full path
                        Path changedFile = dir.resolve((Path) event.context());

                        // I check if this file is one of the monitored ones
                        TailReader r = readers.get(changedFile);
                        if (r != null) {
                            // I instruct the tail reader to fetch newly appended lines
                            r.readNewLines(handler);
                        }
                    }
                }

                // I reset the key so that it continues receiving events
                key.reset();
            }
        } catch (Exception e) {
            // I log that the watcher has stopped
            System.err.println("Watcher stopped: " + e.getMessage());
        }
    }
}
