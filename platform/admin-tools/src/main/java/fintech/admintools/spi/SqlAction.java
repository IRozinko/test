package fintech.admintools.spi;

import fintech.admintools.AdminAction;
import fintech.admintools.AdminActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SqlAction implements AdminAction {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public String getName() {
        return "SQL";
    }

    @Override
    public void execute(AdminActionContext context) {
        jdbcTemplate.execute(context.getParams());
    }
}
