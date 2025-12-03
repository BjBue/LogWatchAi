package bbu.solution.logwatchai.domain.analysis;

import bbu.solution.logwatchai.domain.log.LogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing and executing AI-based log analysis.
 * <p>
 * I use this service to trigger synchronous or asynchronous analysis tasks,
 * retrieve persisted {@link AIAnalysis} results, and query analyses using
 * flexible filter criteria. Implementations of this interface handle
 * persistence, model integration, and retrieval logic.
 * </p>
 */
public interface AIAnalysisService {

    /**
     * Retrieves a single {@link AIAnalysis} by its unique identifier.
     *
     * @param aIAnalysisId the ID of the analysis I want to fetch
     * @return an {@link Optional} containing the analysis if it exists, or empty otherwise
     */
    Optional<AIAnalysis> getAIAnalysisById(UUID aIAnalysisId);

    /**
     * Performs a synchronous AI analysis for the given {@link LogEntry}.
     * <p>
     * I use this when I want to block until the AI model has completed its
     * evaluation and the result has been stored.
     * </p>
     *
     * @param entry the log entry I want to analyze
     * @return the resulting {@link AIAnalysis} entity
     */
    AIAnalysis analyze(LogEntry entry);

    /**
     * Retrieves all stored AI analyses matching the provided filter criteria.
     * <p>
     * Any attribute in the {@link AIAnalysisFilter} may be {@code null},
     * meaning I ignore that field when building the query.
     * </p>
     *
     * @param filter the filter object defining which analyses I want to retrieve
     * @return a list of matching analyses, possibly empty but never {@code null}
     */
    List<AIAnalysis> getAnalysis(AIAnalysisFilter filter);

    /**
     * Retrieves AI analyses matching the provided filter criteria in a paginated format.
     * <p>
     * I use this when I want efficient navigation through larger datasets.
     * </p>
     *
     * @param filter   the search filter describing which analyses I want
     * @param pageable pagination and sorting configuration
     * @return a {@link Page} containing the results for the given page
     */
    Page<AIAnalysis> getAIAnalysisPageable(AIAnalysisFilter filter, Pageable pageable);

}
