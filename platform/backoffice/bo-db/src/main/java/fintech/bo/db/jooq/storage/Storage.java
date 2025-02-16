/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.storage;


import fintech.bo.db.jooq.storage.tables.CloudFile;

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
public class Storage extends SchemaImpl {

    private static final long serialVersionUID = -1221223564;

    /**
     * The reference instance of <code>storage</code>
     */
    public static final Storage STORAGE = new Storage();

    /**
     * The table <code>storage.cloud_file</code>.
     */
    public final CloudFile CLOUD_FILE = fintech.bo.db.jooq.storage.tables.CloudFile.CLOUD_FILE;

    /**
     * No further instances allowed
     */
    private Storage() {
        super("storage", null);
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
            Sequences.CLOUD_FILE_ID_SEQ);
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            CloudFile.CLOUD_FILE);
    }
}
