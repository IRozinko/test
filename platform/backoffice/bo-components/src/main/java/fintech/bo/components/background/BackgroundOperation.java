package fintech.bo.components.background;

@FunctionalInterface
public interface BackgroundOperation<T> {

    T run(BackgroundOperationFeedback feedback) throws Exception;

    default T run() throws Exception {
        return run((message, progress) -> {});
    }
}
