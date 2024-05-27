package fintech.bo.components.agents;


import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.task.tables.records.AgentRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import java.util.ArrayList;
import java.util.List;

import static fintech.bo.db.jooq.task.tables.Agent.AGENT;

public class AgentDataProvider extends JooqDataProvider<AgentRecord> {

    private String textFilter;

    public AgentDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<AgentRecord> buildSelect(Query<AgentRecord, String> query) {
        SelectWhereStep<AgentRecord> select = db.selectFrom(AGENT);
        select.where(AGENT.DISABLED.eq(false));

        query.getFilter().ifPresent(filter -> applyFilter(select, filter));
        if (!StringUtils.isBlank(textFilter)) {
            applyFilter(select, textFilter);
        }

        return select;
    }

    private void applyFilter(SelectWhereStep<AgentRecord> select, String filter) {
        List<Condition> conditions = new ArrayList<>();
        for (String fragment : StringUtils.split(filter, " ")) {
            conditions.add(
                AGENT.EMAIL.startsWith(fragment)
            );
        }
        select.where(conditions);
    }

    public void setTextFilter(String textFilter) {
        this.textFilter = textFilter;
    }

    @Override
    protected Object id(AgentRecord item) {
        return item.getId();
    }

}
