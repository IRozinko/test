package fintech.bo.components.settings;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import fintech.bo.api.client.SettingsApiClient;
import fintech.bo.components.Formats;
import fintech.bo.components.JsonUtils;
import fintech.bo.db.jooq.settings.tables.records.PropertyRecord;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fintech.bo.components.settings.PropertyType.ARRAY;
import static fintech.bo.components.settings.PropertyType.isDate;
import static fintech.bo.components.settings.PropertyType.isDateTime;
import static fintech.bo.db.jooq.settings.Settings.SETTINGS;

@org.springframework.stereotype.Component
public class SettingsComponents {

    @Autowired
    private DSLContext db;

    @Autowired
    private SettingsApiClient settingsApiClient;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        objectMapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);
    }

    public PropertyDataProvider dataProvider() {
        return new PropertyDataProvider(db);
    }

    public EditPropertyDialog editPropertyDialog(PropertyRecord property) {
        return new EditPropertyDialog(settingsApiClient, property);
    }

    public ComboBox<String> names() {
        ComboBox<String> names = new ComboBox<>();
        names.setWidth(500, Sizeable.Unit.PIXELS);
        names.setItems(db.selectDistinct(SETTINGS.PROPERTY.NAME).from(SETTINGS.PROPERTY)
            .orderBy(SETTINGS.PROPERTY.NAME)
            .fetch()
            .map(record -> record.get(SETTINGS.PROPERTY.NAME)));
        return names;
    }

    @SneakyThrows
    public SettingsPanel renderSettingsPanel(String settingName, Map<String, Object> root) {
        SettingsPanel mainPanel = new SettingsPanel(settingName);
        render(mainPanel, root);
        return mainPanel;
    }

    private void render(SettingsPanel parent, Map<String, Object> value) {
        value.entrySet().forEach(entry -> {
            PropertyType type = getType(entry.getValue());
            String name = StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(entry.getKey()), ' '));
            if (type == PropertyType.INNER) {
                SettingsPanel newPanel = new SettingsPanel(name);
                parent.addComponent(newPanel);
                render(newPanel, (Map<String, Object>) entry.getValue());
            } else if (type == ARRAY) {
                List list = (List) entry.getValue();
                if (list.isEmpty())
                    return;

                PropertyType listType = getType(list.get(0));
                if (listType == PropertyType.INNER) {
                    SettingsPanel newPanel = new SettingsPanel(name);
                    parent.addComponent(newPanel);
                    list.forEach(e -> {
                        render(newPanel, (Map<String, Object>) e);
                        newPanel.addComponent(new Label("<hr />", ContentMode.HTML));
                    });
                } else {
                    Binder<Map.Entry<String, Object>> binder = new Binder<>();
                    binder.setBean(entry);
                    parent.addComponent(getInput(binder, entry.getKey(), PropertyType.ARRAY));
                }
            } else {
                Binder<Map.Entry<String, Object>> binder = new Binder<>();
                binder.setBean(entry);
                parent.addComponent(getInput(binder, name, type));
            }
        });
    }

    private PropertyType getType(Object value) {
        if (value instanceof Boolean)
            return PropertyType.BOOLEAN;
        if (value instanceof BigDecimal)
            return PropertyType.DECIMAL;
        if (value instanceof Number)
            return PropertyType.NUMBER;
        if (value instanceof Map)
            return PropertyType.INNER;

        if (value instanceof String && isDate((String) value))
            return PropertyType.DATE;
        if (value instanceof String && isDateTime((String) value))
            return PropertyType.DATETIME;

        if (value instanceof String)
            return PropertyType.TEXT;

        if (value instanceof Collection)
            return PropertyType.ARRAY;

        return PropertyType.TEXT;
    }

    private com.vaadin.ui.Component getInput(Binder<Map.Entry<String, Object>> binder, String name, PropertyType type) {
        AbstractField component = null;
        switch (type) {
            case BOOLEAN:
                CheckBox checkBox = new CheckBox(name);
                binder.bind(checkBox, entry -> (Boolean) entry.getValue(), Map.Entry::setValue);
                component = checkBox;
                break;
            case DATE:
                DateField dateField = new DateField(name);
                dateField.setDateFormat(Formats.DATE_FORMAT);
                binder.forField(dateField)
                    .bind(entry -> (LocalDate) entry.getValue(), Map.Entry::setValue);
                component = dateField;
                break;
            case DATETIME:
                DateTimeField dateTimeField = new DateTimeField(name);
                dateTimeField.setLocale(Locale.UK);
                dateTimeField.setDateFormat(Formats.DATE_TIME_FORMAT);
                binder.forField(dateTimeField)
                    .bind(entry -> (LocalDateTime) entry.getValue(), Map.Entry::setValue);
                component = dateTimeField;
                break;
            case DECIMAL:
                TextField decimalField = new TextField(name);
                binder.forField(decimalField)
                    .withConverter(new StringToBigDecimalConverter("Must be decimal value"))
                    .bind(entry -> (BigDecimal) entry.getValue(), Map.Entry::setValue);
                component = decimalField;
                break;
            case NUMBER:
                TextField numberField = new TextField(name);
                binder.forField(numberField)
                    .withConverter(new StringToLongConverter("Must be number value"))
                    .bind(entry -> (Long) entry.getValue(), Map.Entry::setValue);
                component = numberField;
                break;
            case TEXT:
                TextField field = new TextField(name);
                field.setSizeFull();
                binder.bind(field, entry -> (String) entry.getValue(), Map.Entry::setValue);
                component = field;
                break;
            case ARRAY:
                TextField arrayField = new TextField(name);
                arrayField.setSizeFull();
                binder.bind(arrayField,
                    entry -> JsonUtils.writeValueAsString(entry.getValue()).replaceAll("[\\[\\]\"]", ""),
                    (entry, val) -> {
                        entry.setValue(Stream.of(StringUtils.split(val, ",")).map(String::trim).collect(Collectors.toList()));
                    });
                component = arrayField;
                break;
        }
        component.setWidth(100, Sizeable.Unit.PERCENTAGE);
        return component;
    }

}
