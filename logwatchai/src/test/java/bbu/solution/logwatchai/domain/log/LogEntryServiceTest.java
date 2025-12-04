package bbu.solution.logwatchai.domain.log;

import bbu.solution.logwatchai.application.log.LogEntryServiceImpl;
import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.domain.analysis.AIAnalysisService;
import bbu.solution.logwatchai.domain.decision.DecisionEngineService;
import bbu.solution.logwatchai.infrastructure.persistence.log.LogEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


public class LogEntryServiceTest {

    private LogEntryRepository repository;
    private AIAnalysisService aiAnalysisService;
    private DecisionEngineService decisionEngineService;
    private LogEntryServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(LogEntryRepository.class);
        aiAnalysisService = mock(AIAnalysisService.class);
        decisionEngineService = mock(DecisionEngineService.class);

        service = new LogEntryServiceImpl(repository, aiAnalysisService, decisionEngineService);
    }

    @Test
    void testGetLogEntryById() {
        UUID id = UUID.randomUUID();
        LogEntry entry = new LogEntry("Test", UUID.randomUUID());
        when(repository.findById(id)).thenReturn(Optional.of(entry));

        Optional<LogEntry> result = service.getLogEntryById(id);

        assertTrue(result.isPresent(), "LogEntry should be found");
        assertEquals(entry, result.get(), "Returned entry should match mock");
        verify(repository, times(1)).findById(id);
    }

    @Test
    void testAnalyzeAsync() {
        LogEntry entry = new LogEntry("Log for AI", UUID.randomUUID());
        AIAnalysis analysis = new AIAnalysis();
        analysis.setAnomalyScore(0.9);
        when(repository.findById(entry.getId())).thenReturn(Optional.of(entry));
        when(aiAnalysisService.analyze(entry)).thenReturn(analysis);

        service.analyzeAsync(entry);

        assertTrue(entry.isAnalyzed());
        assertTrue(entry.hasAnomaly());
        verify(repository).save(entry);
        verify(decisionEngineService).evaluate(entry, analysis);
    }

    @Test
    void testDoesLogEntryExistsBySourcveIdRawText(){
        UUID sourceId = UUID.randomUUID();
        String rawMsg = "ERROR ERROR ERROR, World is burning!";

        when(repository.existsBySourceIdAndRawText(sourceId, rawMsg)).thenReturn(Boolean.TRUE);

        boolean exists = service.doesLogEntryExistsBySourceIdRawText(sourceId, rawMsg);

        assertTrue(exists, "Log entry should exist according to the mock repository");
        verify(repository, times(1)).existsBySourceIdAndRawText(sourceId, rawMsg);
    }

    @Test
    void testSaveRawLog(){
        UUID sourceId = UUID.randomUUID();
        String rawMsg = "ERROR ERROR ERROR, World is burning!";

        when(repository.findBySourceIdAndRawText(sourceId, rawMsg)).thenReturn(Optional.empty());

        LogEntry result = service.saveRawLog(rawMsg, sourceId);

        assertNotNull(result, "LogEntry is not allowed to be null");
        assertEquals(rawMsg, result.getRawText(), "rawMsg should be the same");
        assertEquals(sourceId, result.getSourceId(), "sourceId should be the same");

        verify(repository, times(1)).insertIgnoreDuplicate(
                any(), any(), any(Instant.class), any(), any(), any(Instant.class), anyBoolean(), anyBoolean()        );

        verify(repository, times(1)).findBySourceIdAndRawText(sourceId, rawMsg);

    }

}
