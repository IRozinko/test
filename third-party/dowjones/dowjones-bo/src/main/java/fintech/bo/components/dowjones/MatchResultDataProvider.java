package fintech.bo.components.dowjones;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.dowjones.tables.Match.MATCH;
import static fintech.bo.db.jooq.dowjones.tables.Request.REQUEST;
import static fintech.bo.db.jooq.dowjones.tables.SearchResult.SEARCH_RESULT;


@Slf4j
public class MatchResultDataProvider extends JooqDataProvider<Record> {

    private Long dowjonesRequestId;

    public MatchResultDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(fields(MATCH.fields(), REQUEST.CLIENT_ID))
            .from(MATCH)
            .join(SEARCH_RESULT).on(SEARCH_RESULT.ID.eq(MATCH.SEARCH_RESULT_ID))
            .join(REQUEST).on(REQUEST.ID.eq(SEARCH_RESULT.REQUEST_ID));

        if (dowjonesRequestId != null) {
            select.where(SEARCH_RESULT.REQUEST_ID.eq(dowjonesRequestId));
        }
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(MATCH.ID);
    }

    public void setDowjonesRequestId(Long dowjonesRequestId) {
        this.dowjonesRequestId = dowjonesRequestId;
    }
}
