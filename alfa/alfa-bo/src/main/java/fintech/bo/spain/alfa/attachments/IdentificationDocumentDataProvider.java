package fintech.bo.spain.alfa.attachments;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.alfa.tables.records.IdentificationDocumentRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.alfa.Tables.IDENTIFICATION_DOCUMENT;

public class IdentificationDocumentDataProvider extends JooqDataProvider<IdentificationDocumentRecord> {

    private String type;


    private Long clientId;

    public IdentificationDocumentDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<IdentificationDocumentRecord> buildSelect(Query<IdentificationDocumentRecord, String> query) {
        SelectWhereStep<IdentificationDocumentRecord> select = db.selectFrom(IDENTIFICATION_DOCUMENT);

        if (StringUtils.isNotBlank(type)) {
            select.where(IDENTIFICATION_DOCUMENT.DOCUMENT_TYPE.eq(type));
        }

        if (clientId != null) {
            select.where(IDENTIFICATION_DOCUMENT.CLIENT_ID.eq(clientId));
        }

        select.orderBy(IDENTIFICATION_DOCUMENT.ID.desc());

        return select;
    }

    @Override
    protected Object id(IdentificationDocumentRecord item) {
        return item.getId();
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
