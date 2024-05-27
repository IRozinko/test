package fintech.admintools;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class FailingAdminAction implements AdminAction {

    @Override
    public String getName() {
        return "FailingAction";
    }

    @Override
    public void execute(AdminActionContext context) {
        throw new IllegalStateException("I'm failing");
    }
}
