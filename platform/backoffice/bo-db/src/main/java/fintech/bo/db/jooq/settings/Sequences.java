/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.settings;


import javax.annotation.Generated;

import org.jooq.Sequence;
import org.jooq.impl.SequenceImpl;


/**
 * Convenience access to all sequences in settings
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
     * The sequence <code>settings.property_id_seq</code>
     */
    public static final Sequence<Long> PROPERTY_ID_SEQ = new SequenceImpl<Long>("property_id_seq", Settings.SETTINGS, org.jooq.impl.SQLDataType.BIGINT.nullable(false));
}
