/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.rule;


import fintech.bo.db.jooq.rule.tables.RuleLog;
import fintech.bo.db.jooq.rule.tables.RuleSetLog;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in rule
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>rule.rule_log</code>.
     */
    public static final RuleLog RULE_LOG = fintech.bo.db.jooq.rule.tables.RuleLog.RULE_LOG;

    /**
     * The table <code>rule.rule_set_log</code>.
     */
    public static final RuleSetLog RULE_SET_LOG = fintech.bo.db.jooq.rule.tables.RuleSetLog.RULE_SET_LOG;
}
