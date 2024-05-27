package fintech.bo.components.settings;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.SettingsApiClient;
import fintech.bo.api.model.UpdatePropertyRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.JsonUtils;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.SecuredView;
import fintech.bo.db.jooq.settings.tables.records.PropertyRecord;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static fintech.JsonUtils.isJsonValid;
import static fintech.bo.components.settings.PropertyType.TEXT;
import static fintech.bo.db.jooq.settings.Settings.SETTINGS;

@Slf4j
@SecuredView({BackofficePermissions.ADMIN, BackofficePermissions.SETTINGS_EDIT})
@SpringView(name = NewSettingsView.NAME)
public class NewSettingsView extends VerticalLayout implements View {

    public static final String NAME = "new-settings";

    @Autowired
    private SettingsComponents components;

    @Autowired
    private DSLContext db;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SettingsApiClient settingsApiClient;

    private ComboBox<String> settingNames;

    private VerticalLayout settingValues;

    private Button saveButton;

    private Property property;
    private Map<String, Object> jsonRoot;

    @PostConstruct
    public void init() {
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        objectMapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        setCaption("New Settings");
        removeAllComponents();
        HorizontalLayout menuBar = new HorizontalLayout();
        settingNames = components.names();
        settingValues = new VerticalLayout();
        saveButton = new Button("Save");
        saveButton.addStyleNames(ValoTheme.BUTTON_PRIMARY);

        settingValues.setDefaultComponentAlignment(Alignment.TOP_CENTER);

        saveButton.addClickListener(click -> {
            saveSettings();
        });

        settingNames.addValueChangeListener(event -> {
            if (StringUtils.isBlank(event.getValue()))
                return;

            initProperty(event.getValue());
            settingValues.removeAllComponents();
            settingValues.addComponent(components.renderSettingsPanel(event.getValue(), jsonRoot));
        });

        menuBar.addComponents(settingNames, saveButton);
        addComponent(menuBar);
        addComponent(settingValues);
    }

    private void saveSettings() {
        property.setValue(jsonRoot);
        UpdatePropertyRequest request = buildRequest();
        BackgroundOperations.callApi("Saving setting", settingsApiClient.update(request), t -> {
            Notifications.trayNotification("Setting saved");
        }, Notifications::errorNotification);
    }

    private UpdatePropertyRequest buildRequest() {
        UpdatePropertyRequest request = new UpdatePropertyRequest();
        request.setName(property.getName());
        request.setTextValue(property.getTextValue());
        request.setBooleanValue(property.getBooleanValue());
        request.setDateTimeValue(property.getDateTimeValue());
        if (property.getDateValue() != null) {
            request.setDateValue(property.getDateValue());
        }
        request.setNumberValue(property.getNumberValue());
        request.setDecimalValue(property.getDecimalValue());
        return request;
    }

    @SneakyThrows
    private void initProperty(String settingName) {
        PropertyRecord propertyRecord = db.selectFrom(SETTINGS.PROPERTY)
            .where(SETTINGS.PROPERTY.NAME.eq(settingName))
            .fetchOne();
        property = new Property(propertyRecord);
        PropertyType type = PropertyType.valueOf(property.getType());

        if (type == TEXT && isJsonValid(property.getTextValue())) {
            jsonRoot = objectMapper.readValue(property.getTextValue(), new TypeReference<HashMap<String, Object>>() {
            });
        } else {
            jsonRoot = new HashMap<>();
            jsonRoot.put(property.getName(), property.getValue());
        }
    }

}
