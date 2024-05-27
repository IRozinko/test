package fintech.bo.components.client;

import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.security.LoginService;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import static fintech.bo.components.client.JooqClientDataService.CLIENT_NAME_FIELD_NAME;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static org.jooq.impl.DSL.val;

public class ClientDataProviderUtils {

    public static final Field<String> FIELD_CLIENT_NAME = CLIENT.FIRST_NAME.concat(val(" "), CLIENT.LAST_NAME).as(CLIENT_NAME_FIELD_NAME);

    public static Condition deletedClientCondition() {
        if (LoginService.hasPermission(BackofficePermissions.ADMIN)) {
            return DSL.trueCondition();
        }
        return CLIENT.DELETED.isFalse();
    }
}
