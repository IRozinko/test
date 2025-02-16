/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.nordigen;


import fintech.bo.db.jooq.nordigen.tables.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


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
public class Nordigen extends SchemaImpl {

    private static final long serialVersionUID = 772801151;

    /**
     * The reference instance of <code>nordigen</code>
     */
    public static final Nordigen NORDIGEN = new Nordigen();

    /**
     * The table <code>nordigen.log</code>.
     */
    public final Log LOG = fintech.bo.db.jooq.nordigen.tables.Log.LOG;

    /**
     * No further instances allowed
     */
    private Nordigen() {
        super("nordigen", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        List result = new ArrayList();
        result.addAll(getSequences0());
        return result;
    }

    private final List<Sequence<?>> getSequences0() {
        return Arrays.<Sequence<?>>asList(
            Sequences.LOG_ID_SEQ);
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
