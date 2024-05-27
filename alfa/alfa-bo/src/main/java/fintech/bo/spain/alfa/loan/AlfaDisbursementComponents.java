package fintech.bo.spain.alfa.loan;

import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.payments.disbursement.DisbursementComponents;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static fintech.bo.db.jooq.payment.Tables.INSTITUTION;
import static fintech.bo.db.jooq.payment.Tables.INSTITUTION_ACCOUNT;
import static fintech.bo.db.jooq.payment.tables.Disbursement.DISBURSEMENT;
import static fintech.bo.db.jooq.transaction.Tables.TRANSACTION_;

@Component
public class AlfaDisbursementComponents extends DisbursementComponents {

    @Override
    protected void addGridColumns(JooqGridBuilder<Record> builder) {
        Runnable refreshAll = () -> builder.getGrid().getDataProvider().refreshAll();
        builder.addActionColumn(r -> exportButton(r, refreshAll));
        builder.addActionColumn(r -> voidButton(r, refreshAll));

        builder.addColumn(DISBURSEMENT.STATUS).setWidth(100);
        builder.addColumn(DISBURSEMENT.STATUS_DETAIL).setStyleGenerator(statusStyle());
        builder.addColumn(DISBURSEMENT.EXPORTED_AT);
        builder.addColumn(DISBURSEMENT.AMOUNT).setWidth(120);
        builder.addLinkColumn(LOAN.LOAN_NUMBER, r -> LoanComponents.loanLink(r.get(TRANSACTION_.LOAN_ID)));
        builder.addColumn(INSTITUTION.NAME);
        builder.addColumn(INSTITUTION_ACCOUNT.ACCOUNT_NUMBER);
        builder.addColumn(DISBURSEMENT.ERROR);
        builder.addColumn(DISBURSEMENT.VALUE_DATE);
        builder.addAuditColumns(DISBURSEMENT);
        builder.addColumn(DISBURSEMENT.ID);
    }
}
