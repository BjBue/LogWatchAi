package bbu.solution.logwatchai.infrastructure.logwatcher;

import bbu.solution.logwatchai.domain.logwatcher.LogEvent;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class TailReader {

    private long filePointer = 0;
    private final Path file;

    public TailReader(Path file) {
        this.file = file;
    }

    public void initialize() throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "r")) {
            this.filePointer = raf.length();
        }
    }

    public void readNewLines(LineHandler handler) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "r")) {
            long length = raf.length();

            if (length < filePointer) {
                // file rotated / truncated
                filePointer = 0;
            }

            if (length > filePointer) {
                raf.seek(filePointer);

                String line;
                while ((line = raf.readLine()) != null) {
                    handler.handle(new LogEvent(file.toString(), line));
                }

                filePointer = raf.getFilePointer();
            }
        }
    }

    public interface LineHandler {
        void handle(LogEvent event);
    }
}
