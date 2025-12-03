namespace LogWatchAiWebApp.Shared.Models.Login
{
    /// <summary>
    /// Represents the server response returned after a successful login attempt.
    /// </summary>
    public class LoginResponse
    {
        /// <summary>
        /// Gets or sets the authenticated username.
        /// </summary>
        public string Username { get; set; }

        /// <summary>
        /// Gets or sets the role assigned to the authenticated user.
        /// </summary>
        public string Role { get; set; }

        /// <summary>
        /// Gets or sets the issued JWT authentication token.
        /// </summary>
        public string Token { get; set; }

        /// <summary>
        /// Gets or sets the token type (e.g., "Bearer").
        /// </summary>
        public string Type { get; set; }
    }
}