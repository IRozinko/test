/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.storage;


import javax.annotation.Generated;

import org.jooq.Sequence;
import org.jooq.impl.SequenceImpl;


/**
 * Convenience access to all sequences in storage
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Sequences {

    /**
     * The sequence <code>storage.cloud_file_id_seq</code>
     */
    public static final Sequence<Long> CLOUD_FILE_ID_SEQ = new SequenceImpl<Long>("cloud_file_id_seq", Storage.STORAGE, org.jooq.impl.SQLDataType.BIGINT.nullable(false));
}
