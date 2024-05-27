package fintech.bo.components.client;

import com.google.common.collect.ImmutableMap;
import fintech.bo.components.SearchableJooqDataProvider;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectForUpdateStep;

import java.util.Map;
import java.util.function.Function;

import static fintech.bo.components.client.ClientDataProviderUtils.deletedClientCondition;
import static fintech.bo.components.common.SearchFieldOptions.ACCOUNT_NUMBER;
import static fintech.bo.components.common.SearchFieldOptions.DOCUMENT_NUMBER;
import static fintech.bo.components.common.SearchFieldOptions.EMAIL;
import static fintech.bo.components.common.SearchFieldOptions.FIRST_NAME;
import static fintech.bo.components.common.SearchFieldOptions.LAST_NAME;
import static fintech.bo.components.common.SearchFieldOptions.PHONE;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.crm.tables.EmailContact.EMAIL_CONTACT;

public abstract class SearchableJooqClientDataProvider<T extends Record> extends SearchableJooqDataProvider<T> {

    protected static final Map<String, Function<String, Condition>> DEFAULT_CLIENT_SEARCH_FIELDS =
        ImmutableMap.<String, Function<String, Condition>>builder()
            .put(FIRST_NAME, val -> CLIENT.FIRST_NAME.likeIgnoreCase(StringUtils.wrap(val, "%")).and(deletedClientCondition()))
            .put(LAST_NAME, val -> CLIENT.LAST_NAME.likeIgnoreCase(StringUtils.wrap(val, "%")).and(deletedClientCondition()))
            .put(EMAIL, val -> EMAIL_CONTACT.EMAIL.likeIgnoreCase(StringUtils.wrap(val, "%")).and(deletedClientCondition()))
            .put(PHONE, val -> CLIENT.PHONE.likeIgnoreCase(StringUtils.wrap(val, "%")).and(deletedClientCondition()))
            .put(DOCUMENT_NUMBER, val -> CLIENT.DOCUMENT_NUMBER.likeIgnoreCase(StringUtils.wrap(val, "%")).and(deletedClientCondition()))
            .put(ACCOUNT_NUMBER, val -> CLIENT.ACCOUNT_NUMBER.likeIgnoreCase(StringUtils.wrap(val, "%")).and(deletedClientCondition()))
            .build();

    private final JooqClientDataService jooqClientDataService;

    public SearchableJooqClientDataProvider(DSLContext db, Map<String, Function<String, Condition>> searchFields, JooqClientDataService jooqClientDataService) {
        super(db, searchFields);
        this.jooqClientDataService = jooqClientDataService;
    }

    protected Result<T> runQuery(SelectForUpdateStep<T> select) {
        return jooqClientDataService.runQueryHidingDeletedClients(select);
    }
}
