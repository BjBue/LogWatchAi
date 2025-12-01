using System.Net.Http.Json;
using System.Text;
using LogWatchAiWebApp.Shared.Models;

namespace LogWatchAiWebApp.Services
{
    /// <summary>
    /// Provides typed API access to the LogWatchAI backend.
    /// Handles authentication, log retrieval, alerts, and reports.
    /// </summary>
    public class ApiClient
    {
        private readonly HttpClient _http;

        /// <summary>
        /// Creates a new API client using the provided HTTP client instance.
        /// </summary>
        /// <param name="http">The HttpClient configured with the backend base URL.</param>
        public ApiClient(HttpClient http)
        {
            _http = http;
        }

        /// <summary>
        /// Sends a login request to the backend.
        /// </summary>
        /// <param name="user">The username.</param>
        /// <param name="pass">The user's password.</param>
        /// <param name="ct">Optional cancellation token.</param>
        /// <returns>The login response containing the JWT token, or null if unsuccessful.</returns>
        public async Task<LoginResponse?> LoginAsync(string user, string pass, CancellationToken ct = default)
        {
            _http.DefaultRequestHeaders.Authorization = null;
            var body = new LoginRequest { Username = user, Password = pass };
            var resp = await _http.PostAsJsonAsync("auth/login", body, ct);
            if (!resp.IsSuccessStatusCode) return null;
            return await resp.Content.ReadFromJsonAsync<LoginResponse>(cancellationToken: ct);
        }

        /// <summary>
        /// Retrieves all log entries from the backend.
        /// </summary>
        /// <param name="ct">Optional cancellation token.</param>
        /// <returns>A list of log entries, or null if the request failed.</returns>
        public async Task<List<LogEntryDto>?> GetLogsAsync(CancellationToken ct = default)
        {
            var resp = await _http.GetAsync("api/logs", ct);
            if (!resp.IsSuccessStatusCode) return null;
            return await resp.Content.ReadFromJsonAsync<List<LogEntryDto>>(cancellationToken: ct);
        }

        /// <summary>
        /// Retrieves a single log entry by its ID.
        /// </summary>
        /// <param name="id">The ID of the log entry.</param>
        /// <param name="ct">Optional cancellation token.</param>
        /// <returns>The log entry, or null if not found or if the request failed.</returns>
        public async Task<LogEntryDto?> GetLogByIdAsync(string id, CancellationToken ct = default)
        {
            var resp = await _http.GetAsync($"api/logs/{id}", ct);
            if (!resp.IsSuccessStatusCode) return null;
            return await resp.Content.ReadFromJsonAsync<LogEntryDto>(cancellationToken: ct);
        }

        /// <summary>
        /// Performs a backend health check call.
        /// </summary>
        /// <param name="ct">Optional cancellation token.</param>
        /// <returns>The raw health string response, or null if the request failed.</returns>
        public async Task<string?> GetHealthAsync(CancellationToken ct = default)
        {
            var resp = await _http.GetAsync("health", ct);
            if (!resp.IsSuccessStatusCode) return null;
            return await resp.Content.ReadAsStringAsync(ct);
        }

        /// <summary>
        /// Retrieves all active and historical alerts from the backend.
        /// </summary>
        /// <returns>A list of alerts.</returns>
        public async Task<List<AlertDto>> GetAlertsAsync()
        {
            return await _http.GetFromJsonAsync<List<AlertDto>>("api/alerts");
        }

        /// <summary>
        /// Retrieves the daily report for the specified date.
        /// </summary>
        /// <param name="date">A date string in yyyy-MM-dd format.</param>
        /// <returns>The daily report DTO, or null if not found or if the request failed.</returns>
        public async Task<DailyReportDto?> GetDailyReportAsync(string date)
        {
            var url = $"api/report/daily?date={date}&format=json";
            Console.WriteLine($"[ApiClient] Request URL: {_http.BaseAddress}{url}");

            return await _http.GetFromJsonAsync<DailyReportDto>(url);
        }
        
         /// <summary>
        /// Builds a query string from key/value pairs (skips null/empty).
        /// </summary>
        private static string BuildQueryString(Dictionary<string, string?> parameters)
        {
            var sb = new StringBuilder();
            bool first = true;
            foreach (var kv in parameters)
            {
                if (string.IsNullOrEmpty(kv.Value)) continue;
                var encodedKey = Uri.EscapeDataString(kv.Key);
                var encodedValue = Uri.EscapeDataString(kv.Value);
                sb.Append(first ? "?" : "&");
                sb.Append(encodedKey).Append("=").Append(encodedValue);
                first = false;
            }
            return sb.ToString();
        }

        /// <summary>
        /// Retrieves analysis entries applying optional filters.
        /// Uses the GET /api/analysis endpoint and appends query params when provided.
        /// </summary>
        public async Task<List<AnalysisDto>?> GetAnalysisAsync(
            string? id = null,
            string? logEntryId = null,
            string? severity = null,
            string? category = null,
            string? summarizedIssue = null,
            string? likelyCause = null,
            string? recommendation = null,
            double? anomalyScore = null,
            DateTimeOffset? analyzedAt = null,
            CancellationToken ct = default)
        {
            var parameters = new Dictionary<string, string?>
            {
                ["id"] = id,
                ["logEntryId"] = logEntryId,
                ["severity"] = severity,
                ["category"] = category,
                ["summarizedIssue"] = summarizedIssue,
                ["likelyCause"] = likelyCause,
                ["recommendation"] = recommendation,
                ["anomalyScore"] = anomalyScore?.ToString(System.Globalization.CultureInfo.InvariantCulture),
                ["analyzedAt"] = analyzedAt?.ToString("o")
            };

            var url = "api/analysis" + BuildQueryString(parameters);

            try
            {
                return await _http.GetFromJsonAsync<List<AnalysisDto>>(url, cancellationToken: ct);
            }
            catch (HttpRequestException)
            {
                return null;
            }
        }

        /// <summary>
        /// Retrieves a single analysis by id via GET /api/analysis/{id}
        /// </summary>
        public async Task<AnalysisDto?> GetAnalysisByIdAsync(string id, CancellationToken ct = default)
        {
            var resp = await _http.GetAsync($"api/analysis/{Uri.EscapeDataString(id)}", ct);
            if (!resp.IsSuccessStatusCode) return null;
            return await resp.Content.ReadFromJsonAsync<AnalysisDto>(cancellationToken: ct);
        }
        
    }
}
