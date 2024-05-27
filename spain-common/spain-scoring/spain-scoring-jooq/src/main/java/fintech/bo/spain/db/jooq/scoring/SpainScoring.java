/*
 * This file is generated by jOOQ.
*/
package fintech.bo.spain.db.jooq.scoring;


import fintech.bo.spain.db.jooq.scoring.tables.Log;
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
public class SpainScoring extends SchemaImpl {

    private static final long serialVersionUID = 557029558;

    /**
     * The reference instance of <code>spain_scoring</code>
     */
    public static final SpainScoring SPAIN_SCORING = new SpainScoring();

    /**
     * The table <code>spain_scoring.log</code>.
     */
    public final Log LOG = fintech.bo.spain.db.jooq.scoring.tables.Log.LOG;

    /**
     * No further instances allowed
     */
    private SpainScoring() {
        super("spain_scoring", null);
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
            Log.LOG);
    }
}
