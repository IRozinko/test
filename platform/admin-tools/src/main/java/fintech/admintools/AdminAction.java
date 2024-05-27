package fintech.admintools;

public interface AdminAction {

    String getName();

    void execute(AdminActionContext context);
}
