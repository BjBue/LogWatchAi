package bbu.solution.logwatchai.application.analysis;

import bbu.solution.logwatchai.domain.analysis.AIAnalysis;
import bbu.solution.logwatchai.domain.analysis.AIAnalysisService;
import bbu.solution.logwatchai.domain.analysis.Severity;
import bbu.solution.logwatchai.domain.analysis.SeverityUtil;
import bbu.solution.logwatchai.domain.log.LogEntry;
import bbu.solution.logwatchai.infrastructure.persistence.analysis.AIAnalysisRepository;
import com.theokanning.openai.service.OpenAiService;

import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class AIAnalysisServiceImpl implements AIAnalysisService {

    private final OpenAiService openAiService;
    private final AIAnalysisRepository aiRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String model;

    public AIAnalysisServiceImpl(
            @Value("${ai.api-key}") String apiKey,
            @Value("${ai.model:gpt-4o-mini}") String model,
            AIAnalysisRepository aiRepository
    ) {
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(60));
        this.aiRepository = aiRepository;
        this.model = model;
    }

    @Async
    public CompletableFuture<AIAnalysis> analyzeAsync(LogEntry logEntry) {
        AIAnalysis result = analyze(logEntry);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public AIAnalysis analyze(LogEntry logEntry) {
        // 1) create Prompt as json
        String prompt = buildPrompt(logEntry.getRawText());

        ChatMessage system = new ChatMessage("system", "You are an expert log analyst. Return a JSON object with fields: severity, category, summarizedIssue, likelyCause, recommendation, anomalyScore (0.0-1.0). Keep JSON only in response.");
        ChatMessage user = new ChatMessage("user", prompt);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(List.of(system, user))
                .temperature(0.0)
                .maxTokens(700)
                .n(1)
                .build();

        ChatCompletionResult result = openAiService.createChatCompletion(request);
        String text = extractTextFromResult(result);

        // 2) parse text -> AIAnalysis
        AIAnalysis ai = parseAndBuildAIAnalysis(text, logEntry.getId());

        // 3) persist and return
        AIAnalysis saved = aiRepository.save(ai);
        return saved;
    }

    private String buildPrompt(String rawLog) {
        StringBuilder sb = new StringBuilder();
        sb.append("Analyze the following log line and return a JSON object exactly with fields: ");
        sb.append("[\"severity\",\"category\",\"summarizedIssue\",\"likelyCause\",\"recommendation\",\"anomalyScore\"] ");
        sb.append("where severity is one of INFO/WARN/ERROR/DEBUG, anomalyScore is a number between 0.0 and 1.0.\n\n");
        sb.append("Log:\n");
        sb.append(rawLog);
        sb.append("\n\nReturn JSON only.");
        return sb.toString();
    }

    private String extractTextFromResult(ChatCompletionResult result) {
        if (result == null || result.getChoices() == null || result.getChoices().isEmpty()) {
            return "{}";
        }
        ChatCompletionChoice choice = result.getChoices().getFirst();
        ChatMessage msg = choice.getMessage();
        if (msg == null) return "{}";
        return msg.getContent();
    }

    private AIAnalysis parseAndBuildAIAnalysis(String rawResponse, UUID logEntryId) {
        try {
            // JSON -> temporary map
            var node = mapper.readTree(rawResponse);

            String severity = node.path("severity").asText(null);
            String category = node.path("category").asText(null);
            String summarized = node.path("summarizedIssue").asText(null);
            String likelyCause = node.path("likelyCause").asText(null);
            String recommendation = node.path("recommendation").asText(null);
            double score = node.path("anomalyScore").asDouble(0.0);

            AIAnalysis ai = new AIAnalysis(logEntryId,
                    severity != null ? SeverityUtil.valueOfOrNull(severity) : Severity.INFO,
                    category,
                    summarized,
                    likelyCause,
                    recommendation,
                    score
            );

            // optional: store rawResponse field if entity has it - skip if not present
            return ai;
        } catch (Exception e) {
            // Fallback: minimal analysis
            AIAnalysis ai = new AIAnalysis(logEntryId, Severity.INFO, "unknown", "no summary", "no cause", "no recommendation", 0.0);
            return ai;
        }
    }
}
