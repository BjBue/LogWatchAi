using System.Net.Http;
using System.Net.Http.Json;
using LogWatchAiWebApp.Shared.Models;

namespace LogWatchAiWebApp.Services
{
    public class ApiClient
    {
        private readonly HttpClient _http;

        public ApiClient(HttpClient http)
        {
            _http = http;
        }

        public async Task<LoginResponse?> LoginAsync(string user, string pass, CancellationToken ct = default)
        {
            var body = new LoginRequest { Username = user, Password = pass };
            var resp = await _http.PostAsJsonAsync("auth/login", body, ct);
            if (!resp.IsSuccessStatusCode) return null;
            return await resp.Content.ReadFromJsonAsync<LoginResponse>(cancellationToken: ct);
        }

        public async Task<List<LogEntryDto>?> GetLogsAsync(CancellationToken ct = default)
        {
            var resp = await _http.GetAsync("api/logs", ct);
            if (!resp.IsSuccessStatusCode) return null;
            return await resp.Content.ReadFromJsonAsync<List<LogEntryDto>>(cancellationToken: ct);
        }

        public async Task<LogEntryDto?> GetLogByIdAsync(string id, CancellationToken ct = default)
        {
            var resp = await _http.GetAsync($"api/logs/{id}", ct);
            if (!resp.IsSuccessStatusCode) return null;
            return await resp.Content.ReadFromJsonAsync<LogEntryDto>(cancellationToken: ct);
        }

        public async Task<string?> GetHealthAsync(CancellationToken ct = default)
        {
            var resp = await _http.GetAsync("health", ct);
            if (!resp.IsSuccessStatusCode) return null;
            return await resp.Content.ReadAsStringAsync(ct);
        }
        
        public async Task<List<AlertDto>> GetAlertsAsync()
        {
            return await _http.GetFromJsonAsync<List<AlertDto>>("api/alerts");
        }
        
        public async Task<DailyReportDto?> GetDailyReportAsync(string date)
        {
            var url = $"api/report/daily?date={date}&format=json";
            Console.WriteLine($"[ApiClient] Request URL: {_http.BaseAddress}{url}");

            return await _http.GetFromJsonAsync<DailyReportDto>(url);
        }
    }
}