package bbu.solution.logwatchai.domain.alert;

import bbu.solution.logwatchai.domain.analysis.Severity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Immutable filter object used to query alerts based on different optional criteria.
 *
 * <p>This record allows the caller to specify a subset (or all) of the following
 * attributes as filtering constraints. Repository or service layers can then
 * construct dynamic queries based on the provided non-null values.</p>
 *
 * <h2>Fields</h2>
 * <ul>
 *     <li>{@code id} – filters alerts by their unique identifier.</li>
 *     <li>{@code createdAt} – filters alerts created at a specific timestamp.</li>
 *     <li>{@code severity} – filters alerts matching a given severity level.</li>
 *     <li>{@code message} – filters alerts whose message contains or equals this value
 *         (depending on repository implementation).</li>
 *     <li>{@code ruleNames} – filters alerts where at least one of these rule names was triggered.</li>
 *     <li>{@code active} – filters alerts based on their active state.</li>
 *     <li>{@code sourceId} – filters alerts originating from a specific log source.</li>
 *     <li>{@code logEntryId} – filters alerts associated with a specific log entry.</li>
 * </ul>
 *
 * <p>This filter does not enforce any mandatory fields — all criteria are optional.
 * Query builders are expected to check for {@code null} values and apply constraints
 * only when they are present.</p>
 */
public record AlertFilter(
        UUID id,
        Instant createdAt,
        Severity severity,
        String message,
        List<String> ruleNames,
        boolean active,
        UUID sourceId,
        UUID logEntryId
) {}
