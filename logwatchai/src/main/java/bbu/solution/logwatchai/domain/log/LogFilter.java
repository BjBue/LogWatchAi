package bbu.solution.logwatchai.domain.log;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Immutable filter object used for querying log entries. This record groups
 * optional filter criteria such as source, date range, log level, text search,
 * and analysis status flags into a compact, type-safe structure.
 * <p>
 * <b>Why a Java record is the better choice here:</b><br>
 * A {@code record} is ideal for filter objects because:
 * <ul>
 *     <li>Filters are pure data carriers without behavior — records enforce exactly that.</li>
 *     <li>Records are immutable, preventing accidental modification of filter criteria during query processing.</li>
 *     <li>They provide built-in value-based semantics (correct {@code equals}, {@code hashCode}, and {@code toString})
 *         with minimal boilerplate, reducing errors and improving readability.</li>
 *     <li>They make the API clearer by signaling that this type is a simple bundle of parameters.</li>
 * </ul>
 * In contrast, using a mutable POJO for this purpose would offer no benefit, introduce unnecessary verbosity,
 * and risk accidental state changes in multi-step query pipelines.
 */
public record LogFilter(
        UUID sourceId,
        LocalDate fromDate,
        LocalDate toDate,
        String level,
        String containsText,
        Boolean analyzedOnly,
        Boolean hasAnomaly
) {}
