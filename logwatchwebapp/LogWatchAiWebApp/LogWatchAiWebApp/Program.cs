using LogWatchAiWebApp;
using LogWatchAiWebApp.Services;
using Microsoft.AspNetCore.Components.Web;
using Microsoft.AspNetCore.Components.WebAssembly.Hosting;

var builder = WebAssemblyHostBuilder.CreateDefault(args);
builder.RootComponents.Add<App>("#app");
builder.RootComponents.Add<HeadOutlet>("head::after");

// Setze hier die BaseAddress deines Backend:
var backendBase = builder.Configuration["BackendBase"] ?? "http://localhost:8080/";

// HttpClient ohne Auth initially
builder.Services.AddScoped(sp => new HttpClient { BaseAddress = new Uri(backendBase) });

// Registriere Services
builder.Services.AddScoped<ApiClient>();
builder.Services.AddScoped<AuthService>();

var host = builder.Build();

// Initialize Auth token from localStorage before rendering pages
var auth = host.Services.GetRequiredService<AuthService>();
await auth.InitializeFromStorageAsync();

await host.RunAsync();