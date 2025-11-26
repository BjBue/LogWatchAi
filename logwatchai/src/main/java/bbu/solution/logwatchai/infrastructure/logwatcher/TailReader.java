package bbu.solution.logwatchai.infrastructure.logwatcher;

import bbu.solution.logwatchai.domain.logwatcher.LogEvent;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

/**
 * Reads newly appended lines from a file in a tail-like fashion.
 * <p>
 * I keep track of how far I have already read using an internal file pointer.
 * When asked to read new lines, I open the file, compare its current size to my
 * last known pointer, and read only the newly appended data. Each new line is
 * forwarded as a {@link LogEvent} to the provided handler.
 */
public class TailReader {

    /**
     * Stores my current read offset inside the file. I only read forward from here.
     */
    private long filePointer = 0;

    /**
     * The file that I continuously tail.
     */
    private final Path file;

    /**
     * Creates a new TailReader for the given file.
     *
     * @param file The file that I will tail.
     */
    public TailReader(Path file) {
        this.file = file;
    }

    /**
     * Initializes me by setting the file pointer to the end of the file.
     * <p>
     * I do this so that when I begin reading, I only capture newly written lines,
     * not the historical content already in the file.
     *
     * @throws IOException if the file cannot be accessed.
     */
    public void initialize() throws IOException {
        // I open the file for reading
        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "r")) {
            // I set my file pointer to the end of the file
            this.filePointer = raf.length();
        }
    }

    /**
     * Reads and processes any newly appended lines from the file.
     * <p>
     * I compare the current file length to my last known pointer. If the file is shorter,
     * I assume it was truncated or rotated and reset my pointer to 0. If it is longer,
     * I read from the previous pointer forward and forward each new line to the handler.
     *
     * @param handler The callback receiving each new line as a {@link LogEvent}.
     * @throws IOException if the file cannot be accessed.
     */
    public void readNewLines(LineHandler handler) throws IOException {
        // I open the file each time so I always get a fresh view of its current state
        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "r")) {
            long length = raf.length();

            // I detect truncation or rotation by checking if the file is now shorter
            if (length < filePointer) {
                filePointer = 0;
            }

            // If the file grew, I read the new content
            if (length > filePointer) {
                // I move to where I left off
                raf.seek(filePointer);

                String line;
                // I read each new line and send it to the handler
                while ((line = raf.readLine()) != null) {
                    handler.handle(new LogEvent(file.toString(), line));
                }

                // I update my file pointer to the new end
                filePointer = raf.getFilePointer();
            }
        }
    }

    /**
     * Receives each new {@link LogEvent} read by the TailReader.
     */
    public interface LineHandler {
        /**
         * Handles a single new log event.
         *
         * @param event the event representing one newly appended line.
         */
        void handle(LogEvent event);
    }
}
