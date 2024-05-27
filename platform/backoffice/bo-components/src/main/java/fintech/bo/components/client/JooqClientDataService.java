package fintech.bo.components.client;

import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.security.SecuredJooqQuery;
import fintech.bo.components.security.SecuredQuery;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectForUpdateStep;
import org.springframework.stereotype.Component;

import java.util.List;

import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;

@Component
public class JooqClientDataService {
    public static final String CLIENT_FIRST_NAME_FIELD_NAME = "first_name";
    public static final String CLIENT_LAST_NAME_FIELD_NAME = "last_name";
    public static final String CLIENT_EMAIL_FIELD_NAME = "email";
    public static final String CLIENT_PHONE_FIELD_NAME = "phone";
    public static final String CLIENT_DOCUMENT_NUMBER_FIELD_NAME = "document_number";
    public static final String CLIENT_DATE_OF_BIRTH_FIELD_NAME = "date_of_birth";
    public static final String CLIENT_ACCOUNT_NUMBER_FIELD_NAME = "account_number";
    public static final String CLIENT_NAME_FIELD_NAME = "client_name";
    public static final String ACTIVITY_COMMENT_FIELD_NAME = "comment";
    public static final String EMAIL_SEND_TO_FIELD_NAME = "send_to";

    @SecuredJooqQuery(permissions = BackofficePermissions.ADMIN, condition = "#client_deleted == true", template = "'soft_deleted_' + #id",
        fields = {
            CLIENT_FIRST_NAME_FIELD_NAME,
            CLIENT_LAST_NAME_FIELD_NAME,
            CLIENT_EMAIL_FIELD_NAME,
            CLIENT_PHONE_FIELD_NAME,
            CLIENT_DOCUMENT_NUMBER_FIELD_NAME,
            CLIENT_DATE_OF_BIRTH_FIELD_NAME,
            CLIENT_ACCOUNT_NUMBER_FIELD_NAME,
            CLIENT_NAME_FIELD_NAME,
            ACTIVITY_COMMENT_FIELD_NAME,
            EMAIL_SEND_TO_FIELD_NAME
        })
    public <T extends Record> Result<T> runQueryHidingDeletedClients(SelectForUpdateStep<T> select) {
        select.getQuery().addSelect(CLIENT.DELETED.as("client_deleted"));
        return select.fetch();
    }

    @SecuredQuery(permissions = BackofficePermissions.ADMIN, condition = "#deleted == true", template = "'soft_deleted_' + #id")
    public List<ClientDTO> runQueryIntoHidingDeletedClients(SelectForUpdateStep<Record> select) {
        return select.fetchInto(ClientDTO.class);
    }
}
