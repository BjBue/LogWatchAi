using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace LogWatchAiWebApp.Shared.Models
{
    public class DailyReportDto
    {
        // Wenn das Backend ein JSON-Objekt liefert, wird es hier als Dictionary landen.
        // Beispiel: { "errors": 12, "warnings": 5, "byService": { "svc1": 3 } }
        [JsonExtensionData]
        public Dictionary<string, object?>? Data { get; set; }
    }
}