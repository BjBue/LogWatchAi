namespace LogWatchAiWebApp.Shared.Models
{
    /// <summary>
    /// Represents the login request payload containing user credentials.
    /// </summary>
    public class LoginRequest
    {
        /// <summary>
        /// Gets or sets the username used for authentication.
        /// </summary>
        public string Username { get; set; }

        /// <summary>
        /// Gets or sets the password used for authentication.
        /// </summary>
        public string Password { get; set; }
    }
}