package bbu.solution.logwatchai.application.analysis;

import bbu.solution.logwatchai.domain.analysis.*;
import bbu.solution.logwatchai.domain.log.LogEntry;
import bbu.solution.logwatchai.infrastructure.persistence.analysis.AIAnalysisRepository;
import com.theokanning.openai.service.OpenAiService;

import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class AIAnalysisServiceImpl implements AIAnalysisService {

    private final OpenAiService openAiService;
    private final AIAnalysisRepository aiRepository;
    private final String model;

    @Autowired
    private final ObjectMapper mapper;

    /**
     * Constructs the AIAnalysisService with an OpenAI client, repository, and object mapper.
     *
     * @param apiKey the API key for the OpenAI service
     * @param model the model identifier for OpenAI requests
     * @param aiRepository the repository used to persist and retrieve AI analyses
     * @param mapper the JSON object mapper
     */
    public AIAnalysisServiceImpl(
            @Value("${ai.api-key}") String apiKey,
            @Value("${ai.model:gpt-4o-mini}") String model,
            AIAnalysisRepository aiRepository,
            ObjectMapper mapper
    ) {
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(60));
        this.aiRepository = aiRepository;
        this.model = model;
        this.mapper = mapper;
    }

    /**
     * Performs an asynchronous log analysis using the OpenAI model.
     *
     * @param logEntry the log entry to analyze
     * @return a CompletableFuture containing the AIAnalysis result
     */
    @Async("aiExecutor")
    public CompletableFuture<AIAnalysis> analyzeAsync(LogEntry logEntry) {
        AIAnalysis result = analyze(logEntry);
        return CompletableFuture.completedFuture(result);
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
     * Performs a synchronous AI-based analysis on the provided log entry.
     *
     * @param logEntry the log entry to analyze
     * @return the persisted AIAnalysis result
     */
    @Override
    public AIAnalysis analyze(LogEntry logEntry) {
        String prompt = buildPrompt(logEntry.getRawText());

        ChatMessage system = new ChatMessage("system", """
            You are an expert log analyst. 
            Return only JSON with fields:
            severity, category, summarizedIssue, likelyCause, recommendation, anomalyScore
            """);

        ChatMessage user = new ChatMessage("user", prompt);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(List.of(system, user))
                .temperature(0.0)
                .maxTokens(700)
                .n(1)
                .build();

        ChatCompletionResult result = callOpenAIWithRetry(request);

        String text = extractTextFromResult(result);

        AIAnalysis ai = parseAndBuildAIAnalysis(text, logEntry.getId());
        return aiRepository.save(ai);
    }

    /**
     * Calls OpenAI and retries on rate-limit errors with exponential backoff.
     *
     * @param request the OpenAI chat completion request
     * @return the model result
     */
    private ChatCompletionResult callOpenAIWithRetry(ChatCompletionRequest request) {
        int maxRetries = 5;
        int attempt = 0;
        long backoff = 2000; // Initial backoff in milliseconds

        while (true) {
            try {
                return openAiService.createChatCompletion(request);
            }
            catch (com.theokanning.openai.OpenAiHttpException ex) {
                String msg = ex.getMessage();
                if (msg != null && msg.contains("Rate limit")) {
                    attempt++;
                    if (attempt > maxRetries) {
                        throw ex;
                    }

                    try {
                        Thread.sleep(backoff);
                    } catch (InterruptedException ignored) {}

                    backoff *= 2; // exponential backoff
                    continue;
                }

                // If it's not a rate-limit issue, rethrow
                throw ex;
            }
        }
    }

    /**
     * Builds the prompt sent to the AI model for log analysis.
     *
     * @param rawLog the raw log string
     * @return the full prompt text
     */
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

    /**
     * Extracts the plain text content returned by the model from the completion result.
     *
     * @param result the chat completion result
     * @return the text content or an empty JSON object if unavailable
     */
    private String extractTextFromResult(ChatCompletionResult result) {
        if (result == null || result.getChoices() == null || result.getChoices().isEmpty()) {
            return "{}";
        }
        ChatCompletionChoice choice = result.getChoices().getFirst();
        ChatMessage msg = choice.getMessage();
        if (msg == null) return "{}";
        return msg.getContent();
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

            AIAnalysis ai = new AIAnalysis(logEntryId,
                    severity != null ? SeverityUtil.valueOfOrNull(severity) : Severity.INFO,
                    category,
                    summarized,
                    likelyCause,
                    recommendation,
                    score
            );

            return ai;
        } catch (Exception e) {
            AIAnalysis ai = new AIAnalysis(
                    logEntryId,
                    Severity.INFO,
                    "unknown",
                    "no summary",
                    "no cause",
                    "no recommendation",
                    0.0
            );
            return ai;
        }
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
        return aiRepository.findAll();
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
        return aiRepository.findAll(pageable);
    }
}
