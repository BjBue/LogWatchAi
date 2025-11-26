using System.Net.Http.Headers;
using Microsoft.JSInterop;

namespace LogWatchAiWebApp.Services
{
    /// <summary>
    /// Handles JWT persistence, retrieval, and automatic authorization configuration
    /// for the Blazor WebAssembly frontend.
    /// </summary>
    public class AuthService
    {
        private readonly HttpClient _http;
        private readonly IJSRuntime _js;

        /// <summary>
        /// Creates a new authentication service instance.
        /// </summary>
        /// <param name="http">The HttpClient used for all backend API requests.</param>
        /// <param name="js">The JavaScript runtime used to access browser localStorage.</param>
        public AuthService(HttpClient http, IJSRuntime js)
        {
            _http = http;
            _js = js;
        }

        /// <summary>
        /// Stores or clears the JWT in browser localStorage and updates the HttpClient's Authorization header.
        /// </summary>
        /// <param name="token">The JWT token to save, or null/empty to clear authentication.</param>
        public async Task SetTokenAsync(string token)
        {
            if (!string.IsNullOrEmpty(token))
            {
                await _js.InvokeVoidAsync("localStorage.setItem", "jwt", token);
                _http.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", token);
            }
            else
            {
                await _js.InvokeVoidAsync("localStorage.removeItem", "jwt");
                _http.DefaultRequestHeaders.Authorization = null;
            }
        }

        /// <summary>
        /// Retrieves the JWT from browser localStorage.
        /// </summary>
        /// <returns>The stored JWT token, or null if none exists.</returns>
        public async Task<string?> GetTokenAsync()
        {
            return await _js.InvokeAsync<string>("localStorage.getItem", "jwt");
        }

        /// <summary>
        /// Loads the JWT from localStorage and applies it to the HttpClient on application startup.
        /// </summary>
        public async Task InitializeFromStorageAsync()
        {
            var token = await GetTokenAsync();
            if (!string.IsNullOrEmpty(token))
            {
                _http.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", token);
            }
        }
    }
}
