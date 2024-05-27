/*
 * This file is generated by jOOQ.
*/
package fintech.spain.consents.db.jooq;


import fintech.spain.consents.db.jooq.tables.Consent;
import fintech.spain.consents.db.jooq.tables.Terms;
import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SpainConsents extends SchemaImpl {

    private static final long serialVersionUID = -358612079;

    /**
     * The reference instance of <code>spain_consents</code>
     */
    public static final SpainConsents SPAIN_CONSENTS = new SpainConsents();

    /**
     * The table <code>spain_consents.consent</code>.
     */
    public final Consent CONSENT = fintech.spain.consents.db.jooq.tables.Consent.CONSENT;

    /**
     * The table <code>spain_consents.terms</code>.
     */
    public final Terms TERMS = fintech.spain.consents.db.jooq.tables.Terms.TERMS;

    /**
     * No further instances allowed
     */
    private SpainConsents() {
        super("spain_consents", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            Consent.CONSENT,
            Terms.TERMS);
    }
}
