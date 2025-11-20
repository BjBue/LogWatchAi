package bbu.solution.logwatchai.application.rules;

import lombok.Data;
import java.util.List;

@Data
public class RuleDefinition {

    private String name;

    //optional condiditioners
    private String severityAtLeast;
    private String anomalyScoreMin;
    private List<String> textContains;

}
