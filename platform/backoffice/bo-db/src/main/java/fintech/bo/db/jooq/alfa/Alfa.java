/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.alfa;


import fintech.bo.db.jooq.alfa.tables.Address;
import fintech.bo.db.jooq.alfa.tables.AlfaDailyPenaltyStrategy;
import fintech.bo.db.jooq.alfa.tables.AlfaDpdPenaltyStrategy;
import fintech.bo.db.jooq.alfa.tables.AlfaDpdPenaltyStrategyPenalty;
import fintech.bo.db.jooq.alfa.tables.AlfaExtensionStrategy;
import fintech.bo.db.jooq.alfa.tables.AlfaFeeStrategy;
import fintech.bo.db.jooq.alfa.tables.AlfaMonthlyInterestStrategy;
import fintech.bo.db.jooq.alfa.tables.ExtensionDiscount;
import fintech.bo.db.jooq.alfa.tables.IdentificationDocument;
import fintech.bo.db.jooq.alfa.tables.LoanRescheduling;
import fintech.bo.db.jooq.alfa.tables.LocBatch;
import fintech.bo.db.jooq.alfa.tables.Popup;
import fintech.bo.db.jooq.alfa.tables.PopupAttribute;
import fintech.bo.db.jooq.alfa.tables.ViventorExportSettings;
import fintech.bo.db.jooq.alfa.tables.ViventorExtensionLog;
import fintech.bo.db.jooq.alfa.tables.ViventorLoanData;
import fintech.bo.db.jooq.alfa.tables.Wealthiness;
import fintech.bo.db.jooq.alfa.tables.WealthinessCategory;

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
public class Alfa extends SchemaImpl {

    private static final long serialVersionUID = -1369625643;

    /**
     * The reference instance of <code>alfa</code>
     */
    public static final Alfa ALFA = new Alfa();

    /**
     * The table <code>alfa.address</code>.
     */
    public final Address ADDRESS = fintech.bo.db.jooq.alfa.tables.Address.ADDRESS;

    /**
     * The table <code>alfa.alfa_daily_penalty_strategy</code>.
     */
    public final AlfaDailyPenaltyStrategy ALFA_DAILY_PENALTY_STRATEGY = fintech.bo.db.jooq.alfa.tables.AlfaDailyPenaltyStrategy.ALFA_DAILY_PENALTY_STRATEGY;

    /**
     * The table <code>alfa.alfa_dpd_penalty_strategy</code>.
     */
    public final AlfaDpdPenaltyStrategy ALFA_DPD_PENALTY_STRATEGY = fintech.bo.db.jooq.alfa.tables.AlfaDpdPenaltyStrategy.ALFA_DPD_PENALTY_STRATEGY;

    /**
     * The table <code>alfa.alfa_dpd_penalty_strategy_penalty</code>.
     */
    public final AlfaDpdPenaltyStrategyPenalty ALFA_DPD_PENALTY_STRATEGY_PENALTY = fintech.bo.db.jooq.alfa.tables.AlfaDpdPenaltyStrategyPenalty.ALFA_DPD_PENALTY_STRATEGY_PENALTY;

    /**
     * The table <code>alfa.alfa_extension_strategy</code>.
     */
    public final AlfaExtensionStrategy ALFA_EXTENSION_STRATEGY = fintech.bo.db.jooq.alfa.tables.AlfaExtensionStrategy.ALFA_EXTENSION_STRATEGY;

    /**
     * The table <code>alfa.alfa_fee_strategy</code>.
     */
    public final AlfaFeeStrategy ALFA_FEE_STRATEGY = fintech.bo.db.jooq.alfa.tables.AlfaFeeStrategy.ALFA_FEE_STRATEGY;

    /**
     * The table <code>alfa.alfa_monthly_interest_strategy</code>.
     */
    public final AlfaMonthlyInterestStrategy ALFA_MONTHLY_INTEREST_STRATEGY = fintech.bo.db.jooq.alfa.tables.AlfaMonthlyInterestStrategy.ALFA_MONTHLY_INTEREST_STRATEGY;

    /**
     * The table <code>alfa.extension_discount</code>.
     */
    public final ExtensionDiscount EXTENSION_DISCOUNT = fintech.bo.db.jooq.alfa.tables.ExtensionDiscount.EXTENSION_DISCOUNT;

    /**
     * The table <code>alfa.identification_document</code>.
     */
    public final IdentificationDocument IDENTIFICATION_DOCUMENT = fintech.bo.db.jooq.alfa.tables.IdentificationDocument.IDENTIFICATION_DOCUMENT;

    /**
     * The table <code>alfa.loan_rescheduling</code>.
     */
    public final LoanRescheduling LOAN_RESCHEDULING = fintech.bo.db.jooq.alfa.tables.LoanRescheduling.LOAN_RESCHEDULING;

    /**
     * The table <code>alfa.loc_batch</code>.
     */
    public final LocBatch LOC_BATCH = fintech.bo.db.jooq.alfa.tables.LocBatch.LOC_BATCH;

    /**
     * The table <code>alfa.popup</code>.
     */
    public final Popup POPUP = fintech.bo.db.jooq.alfa.tables.Popup.POPUP;

    /**
     * The table <code>alfa.popup_attribute</code>.
     */
    public final PopupAttribute POPUP_ATTRIBUTE = fintech.bo.db.jooq.alfa.tables.PopupAttribute.POPUP_ATTRIBUTE;

    /**
     * The table <code>alfa.viventor_export_settings</code>.
     */
    public final ViventorExportSettings VIVENTOR_EXPORT_SETTINGS = fintech.bo.db.jooq.alfa.tables.ViventorExportSettings.VIVENTOR_EXPORT_SETTINGS;

    /**
     * The table <code>alfa.viventor_extension_log</code>.
     */
    public final ViventorExtensionLog VIVENTOR_EXTENSION_LOG = fintech.bo.db.jooq.alfa.tables.ViventorExtensionLog.VIVENTOR_EXTENSION_LOG;

    /**
     * The table <code>alfa.viventor_loan_data</code>.
     */
    public final ViventorLoanData VIVENTOR_LOAN_DATA = fintech.bo.db.jooq.alfa.tables.ViventorLoanData.VIVENTOR_LOAN_DATA;

    /**
     * The table <code>alfa.wealthiness</code>.
     */
    public final Wealthiness WEALTHINESS = fintech.bo.db.jooq.alfa.tables.Wealthiness.WEALTHINESS;

    /**
     * The table <code>alfa.wealthiness_category</code>.
     */
    public final WealthinessCategory WEALTHINESS_CATEGORY = fintech.bo.db.jooq.alfa.tables.WealthinessCategory.WEALTHINESS_CATEGORY;

    /**
     * No further instances allowed
     */
    private Alfa() {
        super("alfa", null);
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
            Sequences.ADDRESS_ID_SEQ,
            Sequences.LOC_BATCH_SEQUENCE);
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            Address.ADDRESS,
            AlfaDailyPenaltyStrategy.ALFA_DAILY_PENALTY_STRATEGY,
            AlfaDpdPenaltyStrategy.ALFA_DPD_PENALTY_STRATEGY,
            AlfaDpdPenaltyStrategyPenalty.ALFA_DPD_PENALTY_STRATEGY_PENALTY,
            AlfaExtensionStrategy.ALFA_EXTENSION_STRATEGY,
            AlfaFeeStrategy.ALFA_FEE_STRATEGY,
            AlfaMonthlyInterestStrategy.ALFA_MONTHLY_INTEREST_STRATEGY,
            ExtensionDiscount.EXTENSION_DISCOUNT,
            IdentificationDocument.IDENTIFICATION_DOCUMENT,
            LoanRescheduling.LOAN_RESCHEDULING,
            LocBatch.LOC_BATCH,
            Popup.POPUP,
            PopupAttribute.POPUP_ATTRIBUTE,
            ViventorExportSettings.VIVENTOR_EXPORT_SETTINGS,
            ViventorExtensionLog.VIVENTOR_EXTENSION_LOG,
            ViventorLoanData.VIVENTOR_LOAN_DATA,
            Wealthiness.WEALTHINESS,
            WealthinessCategory.WEALTHINESS_CATEGORY);
    }
}
