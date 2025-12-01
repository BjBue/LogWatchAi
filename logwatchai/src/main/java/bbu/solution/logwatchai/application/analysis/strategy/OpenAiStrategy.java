package bbu.solution.logwatchai.application.analysis.strategy;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.*;

import java.time.Duration;
import java.util.List;

/**
 * AI strategy implementation for the OpenAI (theokanning) client.
 * <p>
 * This class encapsulates creation of the OpenAiService and contains a local
 * retry loop for transient errors (rate limit). The retry/backoff logic is
 * intentionally self-contained so each provider can have its own policy.
 */
public class OpenAiStrategy implements AiStrategy {

    private final String name;
    private final String model;
    private final String key;

    private final OpenAiService service;

    public OpenAiStrategy(String name, String model, String key) {
        this.name = name;
        this.model = model;
        this.key = key;

        this.service = new OpenAiService(key, Duration.ofSeconds(60));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEnabled() {
        return key != null && !key.isBlank();
    }

    @Override
    public String analyze(String prompt) throws Exception{

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
        if (result == null || result.getChoices() == null || result.getChoices().isEmpty()) {
            return "{}";
        }
        ChatCompletionChoice choice = result.getChoices().getFirst();
        ChatMessage msg = choice.getMessage();
        return msg == null ? "{}" : msg.getContent();
    }

    /**
     * Retry wrapper for createChatCompletion. Retries on rate-limit errors with exponential backoff.
     *
     * @param request the chat completion request
     * @return the ChatCompletionResult
     * @throws OpenAiHttpException if non-retriable or max retries exceeded
     */
    private ChatCompletionResult callOpenAIWithRetry(ChatCompletionRequest request) {
        int maxRetries = 5;
        int attempt = 0;
        long backoff = 2000; // 2s initial

        while (true) {
            try {
                return service.createChatCompletion(request);
            } catch (OpenAiHttpException ex) {
                String msg = ex.getMessage();
                if (msg != null && msg.contains("Rate limit")) {
                    attempt++;
                    if (attempt > maxRetries) {
                        throw ex; // exceed retries -> bubble up
                    }
                    try {
                        Thread.sleep(backoff);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during backoff", ie);
                    }
                    backoff *= 2;
                    continue;
                }
                // not a rate-limit error -> rethrow
                throw ex;
            }
        }
    }
}
