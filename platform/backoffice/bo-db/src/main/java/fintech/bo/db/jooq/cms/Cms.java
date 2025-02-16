/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.cms;


import fintech.bo.db.jooq.cms.tables.Item;
import fintech.bo.db.jooq.cms.tables.Locale;
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
public class Cms extends SchemaImpl {

    private static final long serialVersionUID = -1706320691;

    /**
     * The reference instance of <code>cms</code>
     */
    public static final Cms CMS = new Cms();

    /**
     * The table <code>cms.item</code>.
     */
    public final Item ITEM = fintech.bo.db.jooq.cms.tables.Item.ITEM;

    /**
     * The table <code>cms.locale</code>.
     */
    public final Locale LOCALE = fintech.bo.db.jooq.cms.tables.Locale.LOCALE;

    /**
     * No further instances allowed
     */
    private Cms() {
        super("cms", null);
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
            Item.ITEM,
            Locale.LOCALE);
    }
}
