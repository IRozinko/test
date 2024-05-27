package fintech.bo.components.activity;

import com.google.common.collect.ImmutableMap;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.bo.api.client.ActivityApiClient;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.db.jooq.settings.tables.records.PropertyRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static fintech.bo.db.jooq.settings.Tables.PROPERTY;

@Component
public class ActivityComponents {

    public static final String ACTIVITY_SETTINGS_PROPERTY = "ActivitySettings";

    private Map<String, Supplier<BulkActionComponent>> bulkActions = Collections.synchronizedMap(new HashMap<>());

    @Autowired
    private DSLContext db;

    @Autowired
    private JooqClientDataService jooqClientDataService;

    @Autowired
    private ActivityApiClient activityApiClient;

    public void registerBulkAction(String type, Supplier<BulkActionComponent> component) {
        this.bulkActions.put(type, component);
    }

    public ActivitySettingsJson getSettings() {
        PropertyRecord record = db.selectFrom(PROPERTY).where(PROPERTY.NAME.eq(ACTIVITY_SETTINGS_PROPERTY)).fetchOne();
        Validate.notNull(record, "Activity settings not found");
        String json = Validate.notBlank(record.getTextValue(), "Empty activity settings");
        return JsonUtils.readValue(json, ActivitySettingsJson.class);
    }

    public AddActivityComponent addActivityComponent(Long clientId) {
        return new AddActivityComponent(activityApiClient, getSettings(), clientId, ImmutableMap.copyOf(bulkActions));
    }


    public ActivityHistoryComponent latestActivities(Long clientId) {
        ActivityDataProvider dataProvider = new ActivityDataProvider(db, jooqClientDataService);
        dataProvider.setClientId(clientId);
        ActivityHistoryComponent historyComponent = new ActivityHistoryComponent();
        historyComponent.setDataProvider(dataProvider);
        return historyComponent;
    }
}
