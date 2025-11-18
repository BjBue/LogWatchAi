package bbu.solution.logwatchai.infrastructure.logwatcher;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryWatcher implements Runnable {

    private final WatchService watchService;
    private final Map<Path, TailReader> readers = new ConcurrentHashMap<>();
    private final TailReader.LineHandler handler;

    public DirectoryWatcher(Path directory, TailReader.LineHandler handler) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.handler = handler;

        directory.register(watchService, ENTRY_MODIFY);
    }

    public void addFile(Path file) throws IOException {
        TailReader reader = new TailReader(file);
        reader.initialize();
        readers.put(file, reader);
    }

    @Override
    public void run() {
        try {
            while (true) {
                WatchKey key = watchService.take();
                Path dir = (Path) key.watchable();

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == ENTRY_MODIFY) {
                        Path changedFile = dir.resolve((Path) event.context());

                        TailReader r = readers.get(changedFile);
                        if (r != null) {
                            r.readNewLines(handler);
                        }
                    }
                }

                key.reset();
            }
        } catch (Exception e) {
            System.err.println("Watcher stopped: " + e.getMessage());
        }
    }
}
