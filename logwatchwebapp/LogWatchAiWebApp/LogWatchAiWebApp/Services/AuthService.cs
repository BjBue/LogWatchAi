using System.Net.Http.Headers;
using Microsoft.JSInterop;

namespace LogWatchAiWebApp.Services
{
    public class AuthService
    {
        private readonly HttpClient _http;
        private readonly IJSRuntime _js;

        public AuthService(HttpClient http, IJSRuntime js)
        {
            _http = http;
            _js = js;
        }

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

        public async Task<string?> GetTokenAsync()
        {
            return await _js.InvokeAsync<string>("localStorage.getItem", "jwt");
        }

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