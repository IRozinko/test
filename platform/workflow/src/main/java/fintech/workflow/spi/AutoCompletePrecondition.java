package fintech.workflow.spi;

public interface AutoCompletePrecondition {
    boolean isTrueFor(ActivityContext context);
}
