package fintech.fintechmarket.exception;

public class ScenarioVersionConflictException extends RuntimeException {

    public ScenarioVersionConflictException() {
        super("Scenario version was changed");
    }

}
