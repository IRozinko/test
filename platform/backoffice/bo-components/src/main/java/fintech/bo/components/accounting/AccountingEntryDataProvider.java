package fintech.bo.components.accounting;

import com.google.common.collect.ImmutableList;
import com.vaadin.data.provider.Query;
import fintech.bo.api.model.accounting.AccountingReportQuery;
import fintech.bo.components.ExportableDataProvider;
import fintech.bo.components.JooqDataProvider;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.Objects;

import static fintech.bo.db.jooq.accounting.tables.Account.ACCOUNT;
import static fintech.bo.db.jooq.accounting.tables.Entry.ENTRY;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.lending.tables.Invoice.INVOICE;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static fintech.bo.db.jooq.transaction.tables.Transaction.TRANSACTION_;
import static org.jooq.impl.DSL.orderBy;

public class AccountingEntryDataProvider extends JooqDataProvider<Record> implements ExportableDataProvider {

    private String accountCode;
    private AccountingReportQuery query = new AccountingReportQuery();

    public AccountingEntryDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        Field<Long> joinTransactionId = ENTRY.TRANSACTION_ID.as("joinTransactionId");
        SelectOnConditionStep<Record2<Long, Integer>> joinStep =
            DSL
                .selectDistinct(joinTransactionId, DSL.denseRank().over(orderBy(ENTRY.TRANSACTION_ID.asc())).as("rank"))
                .from(ENTRY)
                .join(ACCOUNT).on(ENTRY.ACCOUNT_ID.eq(ACCOUNT.ID));

        if (Objects.nonNull(accountCode)) {
            joinStep.where(ACCOUNT.CODE.eq(accountCode));
        }

        if (Objects.nonNull(this.query.getBookingDateFrom())) {
            joinStep.where(ENTRY.BOOKING_DATE.greaterOrEqual(this.query.getBookingDateFrom()));
        }

        if (Objects.nonNull(this.query.getBookingDateTo())) {
            joinStep.where(ENTRY.BOOKING_DATE.lessOrEqual(this.query.getBookingDateTo()));
        }

        if (Objects.nonNull(this.query.getPaymentId())) {
            joinStep.where(ENTRY.PAYMENT_ID.eq(this.query.getPaymentId()));
        }

        if (Objects.nonNull(this.query.getLoanId())) {
            joinStep.where(ENTRY.LOAN_ID.eq(this.query.getLoanId()));
        }

        if (Objects.nonNull(this.query.getClientId())) {
            joinStep.where(ENTRY.CLIENT_ID.eq(this.query.getClientId()));
        }


        Table<Record2<Long, Integer>> joinTable = joinStep.asTable();
        SelectWhereStep<Record> select = db
            .select()
            .from(ENTRY)
            .join(joinTable).on(joinTable.field(joinTransactionId).eq(ENTRY.TRANSACTION_ID))
            .join(ACCOUNT).on(ENTRY.ACCOUNT_ID.eq(ACCOUNT.ID))
            .join(TRANSACTION_).on(ENTRY.TRANSACTION_ID.eq(TRANSACTION_.ID))
            .leftJoin(CLIENT).on(ENTRY.CLIENT_ID.eq(CLIENT.ID))
            .leftJoin(LOAN).on(ENTRY.LOAN_ID.eq(LOAN.ID))
            .leftJoin(INVOICE).on(ENTRY.INVOICE_ID.eq(INVOICE.ID));


        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(ENTRY.ID);
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public void setQuery(AccountingReportQuery query) {
        this.query = query;
    }

    public void setPaymentId(Long paymentId) {
        this.query.setPaymentId(paymentId);
    }

    public void setLoanId(Long loanId) {
        this.query.setLoanId(loanId);
    }

    public void setClientId(Long clientId) {
        this.query.setClientId(clientId);
    }

    public AccountingReportQuery getQuery() {
        return query;
    }

    @Override
    public List<TableField> exportableColumns() {
        return ImmutableList.of(
            ENTRY.TRANSACTION_ID, ENTRY.TRANSACTION_TYPE, ENTRY.BOOKING_DATE, ENTRY.POST_DATE,
            ACCOUNT.NAME, ACCOUNT.CODE, ENTRY.DEBIT, ENTRY.CREDIT,
            LOAN.LOAN_NUMBER, CLIENT.CLIENT_NUMBER, ENTRY.PAYMENT_ID
        );
    }
}
