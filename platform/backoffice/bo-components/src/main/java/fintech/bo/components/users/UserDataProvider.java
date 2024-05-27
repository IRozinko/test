package fintech.bo.components.users;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.security.tables.records.UserRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import java.util.ArrayList;
import java.util.List;

import static fintech.bo.db.jooq.security.Tables.USER;

public class UserDataProvider extends JooqDataProvider<UserRecord> {

    private String textFilter;

    public UserDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<UserRecord> buildSelect(Query<UserRecord, String> query) {
        SelectWhereStep<UserRecord> select = db.selectFrom(USER);
        query.getFilter().ifPresent(filter -> applyFilter(select, filter));
        if (!StringUtils.isBlank(textFilter)) {
            applyFilter(select, textFilter);
        }

        return select;
    }

    private void applyFilter(SelectWhereStep<UserRecord> select, String filter) {
        List<Condition> conditions = new ArrayList<>();
        for (String fragment : StringUtils.split(filter, " ")) {
            conditions.add(
                USER.EMAIL.likeIgnoreCase("%" + fragment + "%")
            );
        }
        select.where(conditions);
    }

    public void setTextFilter(String textFilter) {
        this.textFilter = textFilter;
    }

    @Override
    protected Object id(UserRecord item) {
        return item.getId();
    }
}
