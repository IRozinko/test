package fintech.admintools;

public interface AdminActionContext {

    String getParams();

    void failed(String message);

    void updateProgress(String message);

    boolean isRunning();
}
