/*
 * This file is generated by jOOQ.
*/
package fintech.bo.spain.db.jooq.asnef;


import fintech.bo.spain.db.jooq.asnef.tables.Log;
import fintech.bo.spain.db.jooq.asnef.tables.LogRow;
import fintech.bo.spain.db.jooq.asnef.tables.records.LogRecord;
import fintech.bo.spain.db.jooq.asnef.tables.records.LogRowRecord;
import org.jooq.ForeignKey;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;

import javax.annotation.Generated;


/**
 * A class modelling foreign key relationships between tables of the <code>spain_asnef</code> 
 * schema
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<LogRecord> LOG_PKEY = UniqueKeys0.LOG_PKEY;
    public static final UniqueKey<LogRowRecord> LOG_ROW_PKEY = UniqueKeys0.LOG_ROW_PKEY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<LogRowRecord, LogRecord> LOG_ROW__LOG_ROW_LOG_FK = ForeignKeys0.LOG_ROW__LOG_ROW_LOG_FK;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class UniqueKeys0 extends AbstractKeys {
        public static final UniqueKey<LogRecord> LOG_PKEY = createUniqueKey(Log.LOG, "log_pkey", Log.LOG.ID);
        public static final UniqueKey<LogRowRecord> LOG_ROW_PKEY = createUniqueKey(LogRow.LOG_ROW, "log_row_pkey", LogRow.LOG_ROW.ID);
    }

    private static class ForeignKeys0 extends AbstractKeys {
        public static final ForeignKey<LogRowRecord, LogRecord> LOG_ROW__LOG_ROW_LOG_FK = createForeignKey(fintech.bo.spain.db.jooq.asnef.Keys.LOG_PKEY, LogRow.LOG_ROW, "log_row__log_row_log_fk", LogRow.LOG_ROW.LOG_ID);
    }
}
