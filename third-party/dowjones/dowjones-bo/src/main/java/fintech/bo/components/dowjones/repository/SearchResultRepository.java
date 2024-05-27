package fintech.bo.components.dowjones.repository;

import com.vaadin.data.provider.Query;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.JooqDataRepository;
import fintech.bo.components.dowjones.dto.SearchResultDTO;
import fintech.bo.components.security.SecuredQuery;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectOnConditionStep;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.dowjones.Tables.SEARCH_RESULT;
import static fintech.bo.db.jooq.dowjones.tables.Request.REQUEST;

@Repository
@Transactional(readOnly = true)
public class SearchResultRepository extends JooqDataRepository<SearchResultDTO> {

    private final DSLContext jooq;

    public SearchResultRepository(DSLContext jooq) {
        super(jooq, SearchResultDTO.class);
        this.jooq = jooq;
    }

    @Override
    @SecuredQuery(permissions = BackofficePermissions.ADMIN)
    public SearchResultDTO getRequired(Long id) {
        return jooq.select(SEARCH_RESULT.fields())
            .select(SEARCH_RESULT.ID)
            .select(CLIENT.CLIENT_NUMBER, CLIENT.FIRST_NAME,
                CLIENT.LAST_NAME, CLIENT.SECOND_LAST_NAME, CLIENT.SECOND_FIRST_NAME, CLIENT.PHONE)
            .from(SEARCH_RESULT)
            .join(REQUEST).on(REQUEST.ID.eq(SEARCH_RESULT.REQUEST_ID))
            .join(CLIENT).on(REQUEST.CLIENT_ID.eq(CLIENT.ID))
            .where(SEARCH_RESULT.ID.eq(id))
            .limit(1)
            .fetchOneInto(SearchResultDTO.class);
    }

    @Override
    protected SelectOnConditionStep<Record> buildSelect(Query<SearchResultDTO, String> query) {
        return jooq.select(SEARCH_RESULT.fields())
            .select(CLIENT.CLIENT_NUMBER, CLIENT.FIRST_NAME,
                CLIENT.LAST_NAME, CLIENT.SECOND_LAST_NAME, CLIENT.SECOND_FIRST_NAME, CLIENT.PHONE)
            .from(SEARCH_RESULT)
            .join(REQUEST).on(REQUEST.ID.eq(SEARCH_RESULT.REQUEST_ID))
            .join(CLIENT).on(CLIENT.ID.eq(REQUEST.CLIENT_ID));
    }

    @Override
    protected Object id(SearchResultDTO item) {
        return item.getId();
    }

}
