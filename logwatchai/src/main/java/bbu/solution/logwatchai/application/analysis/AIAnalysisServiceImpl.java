package bbu.solution.logwatchai.application.analysis;

import bbu.solution.logwatchai.application.analysis.strategy.AiStrategy;
import bbu.solution.logwatchai.application.analysis.strategy.AiStrategyFactory;
import bbu.solution.logwatchai.domain.analysis.*;
import bbu.solution.logwatchai.domain.appconfig.AppConfigService;
import bbu.solution.logwatchai.domain.log.LogEntry;
import bbu.solution.logwatchai.infrastructure.persistence.analysis.AIAnalysisRepository;
import bbu.solution.logwatchai.infrastructure.persistence.analysis.AIAnalysisSpecifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * AIAnalysisService implementation that uses pluggable AiStrategy implementations.
 * Initially this uses a single enabled strategy (the first one found). Later we can
 * run multiple strategies in parallel and aggregate results.
 */
@Service
public class AIAnalysisServiceImpl implements AIAnalysisService {

    private final Map<String, AiStrategy> strategies;
    private final AIAnalysisRepository aiRepository;

    @Autowired
    private final ObjectMapper mapper;

    /**
     * Constructs the AIAnalysisService with an OpenAI client, repository, and object mapper.
     *
     * aparam configService the configuration
     * @param aiRepository the repository used to persist and retrieve AI analyses
     * @param mapper the JSON object mapper
     */
    @Autowired
    public AIAnalysisServiceImpl(
            AppConfigService configService,
            AIAnalysisRepository aiRepository,
            ObjectMapper mapper
    ) {
        this.aiRepository = aiRepository;
        this.mapper = mapper;
        this.strategies = AiStrategyFactory.buildStrategies(configService.getConfig().getAi());

        System.out.println("Initialized AI strategies: " + strategies.keySet());
    }

    /**
     * Performs a synchronous AI-based analysis on the provided log entry.
     *
     * @param logEntry the log entry to analyze
     * @return the persisted AIAnalysis result
     */
    @Override
    public AIAnalysis analyze(LogEntry logEntry) {
        if (strategies.isEmpty()) return fallbackAnalysis(logEntry.getId());

        AiStrategy strategy = strategies.values().iterator().next();
        try {
            String prompt = buildPrompt(logEntry.getRawText());
            AIAnalysis ai = parseAndBuildAIAnalysis(strategy.analyze(prompt), logEntry.getId());
            return aiRepository.save(ai);
        } catch (Exception ex) {
            ex.printStackTrace();
            return aiRepository.save(fallbackAnalysis(logEntry.getId()));
        }
    }

    private AIAnalysis fallbackAnalysis(UUID logEntryId) {
        return new AIAnalysis(logEntryId, Severity.INFO, "unknown", "no summary", "no cause", "no recommendation", 0.0);
    }

    /**
     * Builds the prompt sent to the AI model for log analysis.
     *
     * @param rawLog the raw log string
     * @return the full prompt text
     */
    private String buildPrompt(String rawLog) {
        return """
                Analyze the following log line and return a JSON object exactly with fields:
                ["severity","category","summarizedIssue","likelyCause","recommendation","anomalyScore"]
                where severity is one of INFO/WARN/ERROR/DEBUG, anomalyScore is a number between 0.0 and 1.0.

                Log:
                """ + rawLog + "\n\nReturn JSON only.";
    }

    /**
     * Parses the model response JSON and builds the AIAnalysis domain object.
     * Falls back to a minimal analysis if parsing fails.
     *
     * @param rawResponse the raw JSON response from the AI model
     * @param logEntryId the ID of the analyzed log entry
     * @return the constructed AIAnalysis object
     */
    private AIAnalysis parseAndBuildAIAnalysis(String rawResponse, UUID logEntryId) {
        try {
            var node = mapper.readTree(rawResponse);
            String severity = node.path("severity").asText(null);
            String category = node.path("category").asText(null);
            String summarized = node.path("summarizedIssue").asText(null);
            String likelyCause = node.path("likelyCause").asText(null);
            String recommendation = node.path("recommendation").asText(null);
            double score = node.path("anomalyScore").asDouble(0.0);

            return new AIAnalysis(logEntryId,
                    severity != null ? SeverityUtil.valueOfOrNull(severity) : Severity.INFO,
                    category, summarized, likelyCause, recommendation, score);
        } catch (Exception e) {
            e.printStackTrace();
            return fallbackAnalysis(logEntryId);
        }
    }

    /**
     * Retrieves an AIAnalysis entry by its ID.
     *
     * @param id the unique identifier of the AIAnalysis
     * @return an optional containing the analysis if found
     */
    @Override
    public Optional<AIAnalysis> getAIAnalysisById(UUID id) {
        return aiRepository.findById(id);
    }

    /**
     * Retrieves all AIAnalysis entries matching the provided filter.
     * (The filter is currently not applied.)
     *
     * @param filter the filter configuration
     * @return a list of analysis entries
     */
    @Override
    public List<AIAnalysis> getAnalysis(AIAnalysisFilter filter) {
        return aiRepository.findAll(AIAnalysisSpecifications.applyFilter(filter));
    }

    /**
     * Retrieves pageable AIAnalysis data based on the given filter.
     * (The filter is currently not applied.)
     *
     * @param filter the filter configuration
     * @param pageable the pageable settings
     * @return a page of AIAnalysis results
     */
    @Override
    public Page<AIAnalysis> getAIAnalysisPageable(AIAnalysisFilter filter, Pageable pageable) {
        return aiRepository.findAll(AIAnalysisSpecifications.applyFilter(filter), pageable);
    }
}
