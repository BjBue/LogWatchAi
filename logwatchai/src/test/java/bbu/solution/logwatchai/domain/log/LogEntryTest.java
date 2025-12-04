package bbu.solution.logwatchai.domain.log;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LogEntryTest {

    @Test
    void testMarkAsAnalyzedWithAnalysis(){
        UUID sourceId = UUID.randomUUID();
        LogEntry entry = new LogEntry("test log", sourceId);

        AIAnalysis analysis = new AIAnalysis();
        analysis.setAnomalyScore(0.8);

        entry.markAsAnalyzed(analysis);

        assertTrue(entry.isAnalyzed(), "Entry should marked as analyzed");
        assertTrue(entry.hasAnomaly(), "Entry should be detected");
        assertEquals(sourceId, entry.getSourceId(), "sourceId should be the same");
        assertEquals(analysis, entry.getAnalysis(), "analysis should be in LogEntry");

    }

    @Test
    void testMarkAsAnalyzed(){
        UUID sourceId = UUID.randomUUID();
        LogEntry entry = new LogEntry("test log", sourceId);

        entry.markAsAnalyzed();

        assertTrue(entry.isAnalyzed(), "Entry should marked as analyzed");
        assertEquals(sourceId, entry.getSourceId(), "sourceId should be the same");
    }


    @Test
    void testDetectAnomaly(){
        UUID sourceId = UUID.randomUUID();
        LogEntry entry = new LogEntry("another test log", sourceId);

        entry.detectAnomaly(true);
        assertTrue(entry.hasAnomaly(), "Entry should d");
    }
}
