/*
 * This file is generated by jOOQ.
*/
package fintech.bo.spain.db.jooq.experian;


import fintech.bo.spain.db.jooq.experian.tables.CaisDebt;
import fintech.bo.spain.db.jooq.experian.tables.CaisOperaciones;
import fintech.bo.spain.db.jooq.experian.tables.CaisResumen;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in spain_experian
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
     * The table <code>spain_experian.cais_debt</code>.
     */
    public static final CaisDebt CAIS_DEBT = fintech.bo.spain.db.jooq.experian.tables.CaisDebt.CAIS_DEBT;

    /**
     * The table <code>spain_experian.cais_operaciones</code>.
     */
    public static final CaisOperaciones CAIS_OPERACIONES = fintech.bo.spain.db.jooq.experian.tables.CaisOperaciones.CAIS_OPERACIONES;

    /**
     * The table <code>spain_experian.cais_resumen</code>.
     */
    public static final CaisResumen CAIS_RESUMEN = fintech.bo.spain.db.jooq.experian.tables.CaisResumen.CAIS_RESUMEN;
}
