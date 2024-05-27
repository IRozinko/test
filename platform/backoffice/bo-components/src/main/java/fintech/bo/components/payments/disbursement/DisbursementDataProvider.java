package fintech.bo.components.payments.disbursement;

import com.google.common.collect.ImmutableMap;
import com.vaadin.data.provider.Query;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.client.SearchableJooqClientDataProvider;
import fintech.bo.components.common.SearchFieldValue;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

import static fintech.bo.components.client.ClientDataProviderUtils.deletedClientCondition;
import static fintech.bo.components.common.SearchFieldOptions.ALL;
import static fintech.bo.components.common.SearchFieldOptions.DISBURSEMENT_EXPORTED_FILE_NAME;
import static fintech.bo.components.common.SearchFieldOptions.FIRST_NAME;
import static fintech.bo.components.common.SearchFieldOptions.LAST_NAME;
import static fintech.bo.components.common.SearchFieldOptions.LOAN_NUMBER;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static fintech.bo.db.jooq.payment.tables.Disbursement.DISBURSEMENT;
import static fintech.bo.db.jooq.payment.tables.Institution.INSTITUTION;
import static fintech.bo.db.jooq.payment.tables.InstitutionAccount.INSTITUTION_ACCOUNT;

@Slf4j
@Setter
public class DisbursementDataProvider extends SearchableJooqClientDataProvider<Record> {

    private String statusDetail;
    private LocalDate valueDate;
    private Long loanId;
    private Long clientId;
    private LocalDate valueDateTo;
    private LocalDateTime exportedAtTo;
    private Long accountId;
    private BigDecimal amountLessOrEqual;

    private static final Map<String, Function<String, Condition>> searchFields =
        ImmutableMap.<String, Function<String, Condition>>builder()
            .put(LOAN_NUMBER, val -> LOAN.LOAN_NUMBER.likeIgnoreCase(val + "%"))
            .put(FIRST_NAME, val -> CLIENT.FIRST_NAME.likeIgnoreCase(StringUtils.wrap(val, "%")).and(deletedClientCondition()))
            .put(LAST_NAME, val -> CLIENT.LAST_NAME.likeIgnoreCase(StringUtils.wrap(val, "%")).and(deletedClientCondition()))
            .put(DISBURSEMENT_EXPORTED_FILE_NAME, val -> DISBURSEMENT.EXPORTED_FILE_NAME.likeIgnoreCase(StringUtils.wrap(val, "%")))
            .build();

    public DisbursementDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
        super(db, searchFields, jooqClientDataService);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(fields(
                DISBURSEMENT.fields(),
                CLIENT.FIRST_NAME,
                CLIENT.LAST_NAME,
                CLIENT.CLIENT_NUMBER,
                LOAN.LOAN_NUMBER,
                INSTITUTION.NAME,
                INSTITUTION_ACCOUNT.ACCOUNT_NUMBER
            )).from(DISBURSEMENT.leftJoin(CLIENT).on(DISBURSEMENT.CLIENT_ID.eq(CLIENT.ID))
                .leftJoin(LOAN).on(DISBURSEMENT.LOAN_ID.eq(LOAN.ID))
                .leftJoin(INSTITUTION).on(DISBURSEMENT.INSTITUTION_ID.eq(INSTITUTION.ID))
                .leftJoin(INSTITUTION_ACCOUNT).on(DISBURSEMENT.INSTITUTION_ACCOUNT_ID.eq(INSTITUTION_ACCOUNT.ID)));

        query.getFilter().ifPresent(filter -> applySearch(select, new SearchFieldValue(ALL, filter)));
        applySearch(select);
        setFilters();
        applyFilter(select);
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(DISBURSEMENT.ID);
    }

    private void setFilters() {
        resetFilterConditions();
        addFilterCondition(statusDetail, DISBURSEMENT.STATUS_DETAIL::eq);
        addFilterCondition(valueDate, DISBURSEMENT.VALUE_DATE::eq);
        addFilterCondition(loanId, DISBURSEMENT.LOAN_ID::eq);
        addFilterCondition(clientId, DISBURSEMENT.CLIENT_ID::eq);
        addFilterCondition(accountId, DISBURSEMENT.INSTITUTION_ACCOUNT_ID::eq);
        addFilterCondition(valueDateTo, DISBURSEMENT.VALUE_DATE::le);
        addFilterCondition(exportedAtTo, DISBURSEMENT.EXPORTED_AT::le);
        addFilterCondition(amountLessOrEqual, DISBURSEMENT.AMOUNT::le);
    }
}
