using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace LogWatchAiWebApp.Shared.Models
{
    public class PageAlertDto
    {
        [JsonPropertyName("totalPages")]
        public int? TotalPages { get; set; }

        [JsonPropertyName("totalElements")]
        public long? TotalElements { get; set; }

        [JsonPropertyName("size")]
        public int? Size { get; set; }

        [JsonPropertyName("content")]
        public List<AlertDto>? Content { get; set; }

        [JsonPropertyName("number")]
        public int? Number { get; set; }

        [JsonPropertyName("numberOfElements")]
        public int? NumberOfElements { get; set; }

        [JsonPropertyName("first")]
        public bool? First { get; set; }

        [JsonPropertyName("last")]
        public bool? Last { get; set; }
    }
}