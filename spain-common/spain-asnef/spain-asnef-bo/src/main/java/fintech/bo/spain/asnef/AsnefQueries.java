package fintech.bo.spain.asnef;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

import static fintech.bo.spain.db.jooq.asnef.tables.Log.LOG;
import static fintech.bo.spain.db.jooq.asnef.tables.LogRow.LOG_ROW;

@Component
public class AsnefQueries {

    @Autowired
    private DSLContext db;

    public Optional<LocalDate> getLatestFotoaltasByClientId(Long clientId) {
        return db.select(LOG.PREPARED_AT)
            .from(LOG_ROW)
            .innerJoin(LOG).on(
                LOG_ROW.LOG_ID.eq(LOG.ID)
                    .and(LOG.TYPE.eq(AsnefComponents.LOG_TYPE_FOTOALTAS))
            )
            .where(LOG_ROW.CLIENT_ID.eq(clientId))
            .orderBy(LOG.PREPARED_AT.desc())
            .limit(1)
            .fetchOptional()
            .map(r -> r.value1().toLocalDate());
    }
}
