using System.Text.Json.Serialization;

namespace LogWatchAiWebApp.Shared.Models.Dtos
{
    /// <summary>
    /// Represents a paginated response containing alert entries.
    /// </summary>
    public class PageAlertDto
    {
        /// <summary>
        /// Gets or sets the total number of available pages.
        /// </summary>
        [JsonPropertyName("totalPages")]
        public int? TotalPages { get; set; }

        /// <summary>
        /// Gets or sets the total number of elements across all pages.
        /// </summary>
        [JsonPropertyName("totalElements")]
        public long? TotalElements { get; set; }

        /// <summary>
        /// Gets or sets the size of a single page (number of elements per page).
        /// </summary>
        [JsonPropertyName("size")]
        public int? Size { get; set; }

        /// <summary>
        /// Gets or sets the alert items contained in the current page.
        /// </summary>
        [JsonPropertyName("content")]
        public List<AlertDto>? Content { get; set; }

        /// <summary>
        /// Gets or sets the current zero-based page index.
        /// </summary>
        [JsonPropertyName("number")]
        public int? Number { get; set; }

        /// <summary>
        /// Gets or sets the number of elements returned in the current page.
        /// </summary>
        [JsonPropertyName("numberOfElements")]
        public int? NumberOfElements { get; set; }

        /// <summary>
        /// Gets or sets a value indicating whether this is the first page.
        /// </summary>
        [JsonPropertyName("first")]
        public bool? First { get; set; }

        /// <summary>
        /// Gets or sets a value indicating whether this is the last page.
        /// </summary>
        [JsonPropertyName("last")]
        public bool? Last { get; set; }
    }
}