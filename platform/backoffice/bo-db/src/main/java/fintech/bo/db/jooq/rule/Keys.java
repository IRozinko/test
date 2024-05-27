/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.rule;


import fintech.bo.db.jooq.rule.tables.RuleLog;
import fintech.bo.db.jooq.rule.tables.RuleSetLog;
import fintech.bo.db.jooq.rule.tables.records.RuleLogRecord;
import fintech.bo.db.jooq.rule.tables.records.RuleSetLogRecord;

import javax.annotation.Generated;

import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;


/**
 * A class modelling foreign key relationships between tables of the <code>rule</code> 
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

    public static final Identity<RuleLogRecord, Long> IDENTITY_RULE_LOG = Identities0.IDENTITY_RULE_LOG;
    public static final Identity<RuleSetLogRecord, Long> IDENTITY_RULE_SET_LOG = Identities0.IDENTITY_RULE_SET_LOG;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<RuleLogRecord> RULE_LOG_PKEY = UniqueKeys0.RULE_LOG_PKEY;
    public static final UniqueKey<RuleSetLogRecord> RULE_SET_LOG_PKEY = UniqueKeys0.RULE_SET_LOG_PKEY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<RuleLogRecord, RuleSetLogRecord> RULE_LOG__FKGP0I81BDWHD8KC3A1P4KQMV2X = ForeignKeys0.RULE_LOG__FKGP0I81BDWHD8KC3A1P4KQMV2X;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 extends AbstractKeys {
        public static Identity<RuleLogRecord, Long> IDENTITY_RULE_LOG = createIdentity(RuleLog.RULE_LOG, RuleLog.RULE_LOG.ID);
        public static Identity<RuleSetLogRecord, Long> IDENTITY_RULE_SET_LOG = createIdentity(RuleSetLog.RULE_SET_LOG, RuleSetLog.RULE_SET_LOG.ID);
    }

    private static class UniqueKeys0 extends AbstractKeys {
        public static final UniqueKey<RuleLogRecord> RULE_LOG_PKEY = createUniqueKey(RuleLog.RULE_LOG, "rule_log_pkey", RuleLog.RULE_LOG.ID);
        public static final UniqueKey<RuleSetLogRecord> RULE_SET_LOG_PKEY = createUniqueKey(RuleSetLog.RULE_SET_LOG, "rule_set_log_pkey", RuleSetLog.RULE_SET_LOG.ID);
    }

    private static class ForeignKeys0 extends AbstractKeys {
        public static final ForeignKey<RuleLogRecord, RuleSetLogRecord> RULE_LOG__FKGP0I81BDWHD8KC3A1P4KQMV2X = createForeignKey(fintech.bo.db.jooq.rule.Keys.RULE_SET_LOG_PKEY, RuleLog.RULE_LOG, "rule_log__fkgp0i81bdwhd8kc3a1p4kqmv2x", RuleLog.RULE_LOG.RULE_SET_RESULT_ID);
    }
}
