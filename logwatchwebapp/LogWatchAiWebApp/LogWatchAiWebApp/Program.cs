using LogWatchAiWebApp;
using LogWatchAiWebApp.Services;
using Microsoft.AspNetCore.Components.Web;
using Microsoft.AspNetCore.Components.WebAssembly.Hosting;

/// <summary>
/// Entry point for the Blazor WebAssembly application.
/// Configures dependency injection, HTTP client, and authentication
/// initialization before the app is rendered.
/// </summary>
var builder = WebAssemblyHostBuilder.CreateDefault(args);

/// <summary>
/// Registers the main application root components.
/// </summary>
builder.RootComponents.Add<App>("#app");
builder.RootComponents.Add<HeadOutlet>("head::after");

/// <summary>
/// Sets the base address of the backend API.
/// Falls back to http://localhost:8080/ if no configuration value is provided.
/// </summary>
var backendBase = builder.Configuration["BackendBase"] ?? "http://localhost:8080/";

/// <summary>
/// Registers an HttpClient without authentication for initial usage
/// (authentication token will be injected later).
/// </summary>
builder.Services.AddScoped(sp => new HttpClient { BaseAddress = new Uri(backendBase) });

/// <summary>
/// Registers the application services such as the API client and authentication service.
/// </summary>
builder.Services.AddScoped<ApiClient>();
builder.Services.AddScoped<AuthService>();

var host = builder.Build();

/// <summary>
/// Initializes the authentication token from localStorage before
/// the application renders any pages.
/// Ensures the user stays logged in across browser sessions.
/// </summary>
var auth = host.Services.GetRequiredService<AuthService>();
await auth.InitializeFromStorageAsync();

/// <summary>
/// Runs the Blazor WebAssembly application.
/// </summary>
await host.RunAsync();