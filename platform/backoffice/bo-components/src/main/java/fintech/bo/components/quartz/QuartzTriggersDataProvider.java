package fintech.bo.components.quartz;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;

import java.time.LocalDateTime;

import static fintech.bo.db.jooq.quartz.Tables.QRTZ_PAUSED_TRIGGER_GRPS;
import static fintech.bo.db.jooq.quartz.tables.QrtzTriggers.QRTZ_TRIGGERS;


public class QuartzTriggersDataProvider extends JooqDataProvider<Record> {

    public static final Field<LocalDateTime> FIELD_NEXT_FIRE_TIME_DATE = DSL.field("to_timestamp(next_fire_time / 1000)", LocalDateTime.class)
        .as("next_fire_time_date");
    public static final Field<LocalDateTime> FIELD_PREV_FIRE_TIME_DATE = DSL.field("to_timestamp(prev_fire_time / 1000)", LocalDateTime.class)
        .as("prev_fire_time_date");
    public static final Field<Boolean> FIELD_PAUSED_TRIGGER = DSL.when(QRTZ_PAUSED_TRIGGER_GRPS.TRIGGER_GROUP.isNotNull(), true)
        .otherwise(false)
        .as("paused");

    public QuartzTriggersDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        return db
            .select(fields(
                QRTZ_TRIGGERS.fields(),
                FIELD_NEXT_FIRE_TIME_DATE,
                FIELD_PREV_FIRE_TIME_DATE,
                FIELD_PAUSED_TRIGGER))
            .from(QRTZ_TRIGGERS)
            .leftJoin(QRTZ_PAUSED_TRIGGER_GRPS).on(QRTZ_TRIGGERS.JOB_NAME.eq(QRTZ_PAUSED_TRIGGER_GRPS.TRIGGER_GROUP));
    }

    @Override
    protected Object id(Record item) {
        return item.get(QRTZ_TRIGGERS.TRIGGER_NAME);
    }
}
