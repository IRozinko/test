/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.viventor;


import fintech.bo.db.jooq.viventor.tables.Log;
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
public class Viventor extends SchemaImpl {

    private static final long serialVersionUID = -1884435769;

    /**
     * The reference instance of <code>viventor</code>
     */
    public static final Viventor VIVENTOR = new Viventor();

    /**
     * The table <code>viventor.log</code>.
     */
    public final Log LOG = fintech.bo.db.jooq.viventor.tables.Log.LOG;

    /**
     * No further instances allowed
     */
    private Viventor() {
        super("viventor", null);
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
