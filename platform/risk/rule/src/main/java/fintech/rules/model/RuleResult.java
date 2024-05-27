package fintech.rules.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@ToString(of = {"ruleName", "decision", "reason"})
public class RuleResult {

    private final String ruleName;
    private final Decision decision;
    private final String reason;
    private final String reasonDetails;
    private final List<Check> checks;

    public RuleResult(String ruleName, Decision decision, String reason, String reasonDetails, List<Check> checks) {
        this.ruleName = ruleName;
        this.decision = decision;
        this.reason = reason;
        this.reasonDetails = reasonDetails;
        this.checks = checks;
    }

    @Data
    public static class Check {
        private final String name;
        private final Object expectedValue;
        private final Object evaluatedValue;

        public Check(String name, Object expectedValue, Object evaluatedValue) {
            this.name = name;
            this.expectedValue = expectedValue;
            this.evaluatedValue = evaluatedValue;
        }
    }
}
