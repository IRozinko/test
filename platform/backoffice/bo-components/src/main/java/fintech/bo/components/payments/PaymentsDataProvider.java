package fintech.bo.components.payments;

import com.google.common.collect.ImmutableMap;
import com.vaadin.data.provider.Query;
import fintech.bo.components.SearchableJooqDataProvider;
import fintech.bo.components.common.SearchFieldValue;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;

import static fintech.bo.components.common.SearchFieldOptions.ALL;
import static fintech.bo.components.common.SearchFieldOptions.PAYMENT_COUNTERPARTY_ACCOUNT;
import static fintech.bo.components.common.SearchFieldOptions.PAYMENT_COUNTERPARTY_NAME;
import static fintech.bo.components.common.SearchFieldOptions.PAYMENT_DETAILS;
import static fintech.bo.components.common.SearchFieldOptions.PAYMENT_REFERENCE;
import static fintech.bo.db.jooq.payment.tables.InstitutionAccount.INSTITUTION_ACCOUNT;
import static fintech.bo.db.jooq.payment.tables.Payment.PAYMENT_;

@Slf4j
@Setter
public class PaymentsDataProvider extends SearchableJooqDataProvider<Record> {

    private String statusDetail;
    private String type;
    private LocalDate valueDateFrom;
    private LocalDate valueDateTo;
    private Long institutionId;

    private static final Map<String, Function<String, Condition>> searchFields =
        ImmutableMap.<String, Function<String, Condition>>builder()
            .put(PAYMENT_DETAILS, val -> PAYMENT_.DETAILS.likeIgnoreCase(StringUtils.wrap(val, "%")))
            .put(PAYMENT_COUNTERPARTY_ACCOUNT, val -> PAYMENT_.COUNTERPARTY_ACCOUNT.likeIgnoreCase(StringUtils.wrap(val, "%")))
            .put(PAYMENT_COUNTERPARTY_NAME, val -> PAYMENT_.COUNTERPARTY_NAME.likeIgnoreCase(StringUtils.wrap(val, "%")))
            .put(PAYMENT_REFERENCE, val -> PAYMENT_.REFERENCE.likeIgnoreCase(StringUtils.wrap(val, "%")))
            .build();

    public PaymentsDataProvider(DSLContext db) {
        super(db, searchFields);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = PaymentQueries.summaryQuery(db);
        query.getFilter().ifPresent(filter -> applySearch(select, new SearchFieldValue(ALL, filter)));
        applySearch(select);
        setFilters();
        applyFilter(select);
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(PAYMENT_.ID);
    }

    protected void setFilters() {
        resetFilterConditions();
        addFilterCondition(statusDetail, PAYMENT_.STATUS_DETAIL::eq);
        addFilterCondition(type, PAYMENT_.PAYMENT_TYPE::eq);
        addFilterCondition(valueDateFrom, PAYMENT_.VALUE_DATE::ge);
        addFilterCondition(valueDateTo, PAYMENT_.VALUE_DATE::le);
        addFilterCondition(institutionId, INSTITUTION_ACCOUNT.INSTITUTION_ID::eq);
    }
}
