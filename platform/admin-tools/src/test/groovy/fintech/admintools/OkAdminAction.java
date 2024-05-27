package fintech.admintools;

import fintech.Validate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class OkAdminAction implements AdminAction {

    @Override
    public String getName() {
        return "OkAction";
    }

    @Override
    public void execute(AdminActionContext context) {
        Validate.notNull(context.getParams());
        context.updateProgress("OK");
    }
}
