package fintech.bo.components.users;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.security.tables.records.RoleRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import java.util.ArrayList;
import java.util.List;

import static fintech.bo.db.jooq.security.Security.SECURITY;
import static fintech.bo.db.jooq.security.Tables.ROLE;

public class RoleDataProvider extends JooqDataProvider<RoleRecord> {

    private String textFilter;

    public RoleDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<RoleRecord> buildSelect(Query<RoleRecord, String> query) {
        SelectWhereStep<RoleRecord> select = db.selectFrom(ROLE);

        query.getFilter().ifPresent(filter -> applyFilter(select, filter));
        if (!StringUtils.isBlank(textFilter)) {
            applyFilter(select, textFilter);
        }
        return select;
    }

    private void applyFilter(SelectWhereStep<RoleRecord> select, String filter) {
        List<Condition> conditions = new ArrayList<>();
        for (String fragment : StringUtils.split(filter, " ")) {
            conditions.add(
                SECURITY.ROLE.NAME.likeIgnoreCase("%" + fragment + "%")
            );
        }
        select.where(conditions);
    }

    public void setTextFilter(String textFilter) {
        this.textFilter = textFilter;
    }

    @Override
    protected Object id(RoleRecord item) {
        return item.getName();
    }
}
