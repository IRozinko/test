/*
 * This file is generated by jOOQ.
*/
package fintech.spain.consents.db.jooq;


import fintech.spain.consents.db.jooq.tables.Consent;
import fintech.spain.consents.db.jooq.tables.Terms;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in spain_consents
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
     * The table <code>spain_consents.consent</code>.
     */
    public static final Consent CONSENT = fintech.spain.consents.db.jooq.tables.Consent.CONSENT;

    /**
     * The table <code>spain_consents.terms</code>.
     */
    public static final Terms TERMS = fintech.spain.consents.db.jooq.tables.Terms.TERMS;
}
