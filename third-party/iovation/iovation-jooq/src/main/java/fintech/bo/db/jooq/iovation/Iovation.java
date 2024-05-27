/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.iovation;


import fintech.bo.db.jooq.iovation.tables.Blackbox;
import fintech.bo.db.jooq.iovation.tables.Transaction;
import fintech.bo.db.jooq.iovation.tables.TransactionDetail;
import org.jooq.Catalog;
import org.jooq.Sequence;
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
public class Iovation extends SchemaImpl {

    private static final long serialVersionUID = 359617710;

    /**
     * The reference instance of <code>iovation</code>
     */
    public static final Iovation IOVATION = new Iovation();

    /**
     * The table <code>iovation.blackbox</code>.
     */
    public final Blackbox BLACKBOX = fintech.bo.db.jooq.iovation.tables.Blackbox.BLACKBOX;

    /**
     * The table <code>iovation.transaction</code>.
     */
    public final Transaction TRANSACTION = fintech.bo.db.jooq.iovation.tables.Transaction.TRANSACTION;

    /**
     * The table <code>iovation.transaction_detail</code>.
     */
    public final TransactionDetail TRANSACTION_DETAIL = fintech.bo.db.jooq.iovation.tables.TransactionDetail.TRANSACTION_DETAIL;

    /**
     * No further instances allowed
     */
    private Iovation() {
        super("iovation", null);
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
            Sequences.BLACKBOX_ID_SEQ,
            Sequences.TRANSACTION_ID_SEQ);
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            Blackbox.BLACKBOX,
            Transaction.TRANSACTION,
            TransactionDetail.TRANSACTION_DETAIL);
    }
}
