package fintech.bo.spain.alfa.scoring;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.decision_engine.DecisionEngine.DECISION_ENGINE;
import static fintech.bo.db.jooq.decision_engine.tables.Request.REQUEST;


@Slf4j
public class ScoringDataProvider extends JooqDataProvider<Record> {

    private Long scoringDERequestId;

    public ScoringDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(DECISION_ENGINE.REQUEST.fields())
                .from(DECISION_ENGINE.REQUEST);
        if (scoringDERequestId != null) {
            select.where(DECISION_ENGINE.REQUEST.ID.eq(scoringDERequestId));
        }
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(REQUEST.ID);
    }

    public void setScoringDERequestId(Long scoringDERequestId) {
        this.scoringDERequestId = scoringDERequestId;
    }

}
