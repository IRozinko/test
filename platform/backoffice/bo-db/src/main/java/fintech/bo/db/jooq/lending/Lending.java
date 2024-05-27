/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.lending;


import fintech.bo.db.jooq.lending.tables.CreditLimit;
import fintech.bo.db.jooq.lending.tables.Discount;
import fintech.bo.db.jooq.lending.tables.Installment;
import fintech.bo.db.jooq.lending.tables.Invoice;
import fintech.bo.db.jooq.lending.tables.InvoiceItem;
import fintech.bo.db.jooq.lending.tables.Loan;
import fintech.bo.db.jooq.lending.tables.LoanApplication;
import fintech.bo.db.jooq.lending.tables.LoanApplicationAttribute;
import fintech.bo.db.jooq.lending.tables.LoanContract;
import fintech.bo.db.jooq.lending.tables.LoanDailySnapshot;
import fintech.bo.db.jooq.lending.tables.Period;
import fintech.bo.db.jooq.lending.tables.Product;
import fintech.bo.db.jooq.lending.tables.PromoCode;
import fintech.bo.db.jooq.lending.tables.PromoCodeClient;
import fintech.bo.db.jooq.lending.tables.PromoCodeSource;
import fintech.bo.db.jooq.lending.tables.Schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
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
public class Lending extends SchemaImpl {

    private static final long serialVersionUID = 658100089;

    /**
     * The reference instance of <code>lending</code>
     */
    public static final Lending LENDING = new Lending();

    /**
     * The table <code>lending.credit_limit</code>.
     */
    public final CreditLimit CREDIT_LIMIT = fintech.bo.db.jooq.lending.tables.CreditLimit.CREDIT_LIMIT;

    /**
     * The table <code>lending.discount</code>.
     */
    public final Discount DISCOUNT = fintech.bo.db.jooq.lending.tables.Discount.DISCOUNT;

    /**
     * The table <code>lending.installment</code>.
     */
    public final Installment INSTALLMENT = fintech.bo.db.jooq.lending.tables.Installment.INSTALLMENT;

    /**
     * The table <code>lending.invoice</code>.
     */
    public final Invoice INVOICE = fintech.bo.db.jooq.lending.tables.Invoice.INVOICE;

    /**
     * The table <code>lending.invoice_item</code>.
     */
    public final InvoiceItem INVOICE_ITEM = fintech.bo.db.jooq.lending.tables.InvoiceItem.INVOICE_ITEM;

    /**
     * The table <code>lending.loan</code>.
     */
    public final Loan LOAN = fintech.bo.db.jooq.lending.tables.Loan.LOAN;

    /**
     * The table <code>lending.loan_application</code>.
     */
    public final LoanApplication LOAN_APPLICATION = fintech.bo.db.jooq.lending.tables.LoanApplication.LOAN_APPLICATION;

    /**
     * The table <code>lending.loan_application_attribute</code>.
     */
    public final LoanApplicationAttribute LOAN_APPLICATION_ATTRIBUTE = fintech.bo.db.jooq.lending.tables.LoanApplicationAttribute.LOAN_APPLICATION_ATTRIBUTE;

    /**
     * The table <code>lending.loan_contract</code>.
     */
    public final LoanContract LOAN_CONTRACT = fintech.bo.db.jooq.lending.tables.LoanContract.LOAN_CONTRACT;

    /**
     * The table <code>lending.loan_daily_snapshot</code>.
     */
    public final LoanDailySnapshot LOAN_DAILY_SNAPSHOT = fintech.bo.db.jooq.lending.tables.LoanDailySnapshot.LOAN_DAILY_SNAPSHOT;

    /**
     * The table <code>lending.period</code>.
     */
    public final Period PERIOD = fintech.bo.db.jooq.lending.tables.Period.PERIOD;

    /**
     * The table <code>lending.product</code>.
     */
    public final Product PRODUCT = fintech.bo.db.jooq.lending.tables.Product.PRODUCT;

    /**
     * The table <code>lending.promo_code</code>.
     */
    public final PromoCode PROMO_CODE = fintech.bo.db.jooq.lending.tables.PromoCode.PROMO_CODE;

    /**
     * The table <code>lending.promo_code_client</code>.
     */
    public final PromoCodeClient PROMO_CODE_CLIENT = fintech.bo.db.jooq.lending.tables.PromoCodeClient.PROMO_CODE_CLIENT;

    /**
     * The table <code>lending.promo_code_source</code>.
     */
    public final PromoCodeSource PROMO_CODE_SOURCE = fintech.bo.db.jooq.lending.tables.PromoCodeSource.PROMO_CODE_SOURCE;

    /**
     * The table <code>lending.schedule</code>.
     */
    public final Schedule SCHEDULE = fintech.bo.db.jooq.lending.tables.Schedule.SCHEDULE;

    /**
     * No further instances allowed
     */
    private Lending() {
        super("lending", null);
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
            CreditLimit.CREDIT_LIMIT,
            Discount.DISCOUNT,
            Installment.INSTALLMENT,
            Invoice.INVOICE,
            InvoiceItem.INVOICE_ITEM,
            Loan.LOAN,
            LoanApplication.LOAN_APPLICATION,
            LoanApplicationAttribute.LOAN_APPLICATION_ATTRIBUTE,
            LoanContract.LOAN_CONTRACT,
            LoanDailySnapshot.LOAN_DAILY_SNAPSHOT,
            Period.PERIOD,
            Product.PRODUCT,
            PromoCode.PROMO_CODE,
            PromoCodeClient.PROMO_CODE_CLIENT,
            PromoCodeSource.PROMO_CODE_SOURCE,
            Schedule.SCHEDULE);
    }
}
