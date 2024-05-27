package fintech.bo.components.settings;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.ui.*;
import fintech.bo.api.client.SettingsApiClient;
import fintech.bo.api.model.UpdatePropertyRequest;
import fintech.bo.components.Formats;
import fintech.bo.components.JsonUtils;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.settings.tables.records.PropertyRecord;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;

import java.util.Locale;

public class EditPropertyDialog extends ActionDialog {

    private SettingsApiClient settingsApiClient;
    private PropertyRecord propertyRecord;
    private Binder<PropertyRecord> binder;

    public EditPropertyDialog(SettingsApiClient settingsApiClient, PropertyRecord propertyRecord) {
        super("Edit Property", "Save");
        this.settingsApiClient = settingsApiClient;
        this.propertyRecord = propertyRecord;
        setModal(true);
        setWidth(600, Unit.PIXELS);
        setDialogContent(buildForm());
    }

    private Component buildForm() {
        binder = new Binder<>();
        binder.setBean(propertyRecord);

        FormLayout form = new FormLayout();
        form.setWidthUndefined();
        form.addComponent(nameField());
        form.addComponent(getInput());
        form.setSizeFull();
        return form;
    }

    private TextField nameField() {
        TextField nameField = new TextField("Name");
        nameField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(nameField, PropertyRecord::getName, PropertyRecord::setName);
        nameField.setReadOnly(true);
        return nameField;
    }

    private Component getInput() {
        AbstractField component = null;
        switch (propertyRecord.getType()) {
            case "BOOLEAN":
                CheckBox checkBox = new CheckBox("Value");
                binder.forField(checkBox)
                    .bind(PropertyRecord::getBooleanValue, PropertyRecord::setBooleanValue);
                component = checkBox;
                break;
            case "DATE":
                DateField dateField = new DateField("Value");
                dateField.setDateFormat(Formats.DATE_FORMAT);
                binder.forField(dateField)
                    .bind(PropertyRecord::getDateValue, PropertyRecord::setDateValue);
                component = dateField;
                break;
            case "DATETIME":
                DateTimeField dateTimeField = new DateTimeField("Value");
                dateTimeField.setLocale(Locale.UK);
                dateTimeField.setDateFormat(Formats.DATE_TIME_FORMAT);
                binder.forField(dateTimeField)
                    .bind(PropertyRecord::getDateTimeValue, PropertyRecord::setDateTimeValue);
                component = dateTimeField;
                break;
            case "DECIMAL":
                TextField decimalField = new TextField("Value");
                binder.forField(decimalField)
                        .withConverter(new StringToBigDecimalConverter("Must be decimal value"))
                        .bind(PropertyRecord::getDecimalValue, PropertyRecord::setDecimalValue);
                component = decimalField;
                break;
            case "NUMBER":
                TextField numberField = new TextField("Value");
                binder.forField(numberField)
                        .withConverter(new StringToLongConverter("Must be number value"))
                        .bind(PropertyRecord::getNumberValue, PropertyRecord::setNumberValue);
                component = numberField;
                break;
            case "TEXT":
                if (JsonUtils.isValidJson(propertyRecord.getTextValue())) {
                    AceEditor jsonEditor = new AceEditor();
                    jsonEditor.setMode(AceMode.json);
                    jsonEditor.setHeight(400, Unit.PIXELS);
                    binder.forField(jsonEditor)
                        .withValidator(JsonUtils::isValidJson, "JSON is invalid")
                        .bind(PropertyRecord::getTextValue, PropertyRecord::setTextValue);
                    component = jsonEditor;
                } else {
                    TextArea field = new TextArea("Value", propertyRecord.getTextValue());
                    field.setRows(20);
                    field.setSizeFull();
                    binder.forField(field)
                        .bind(PropertyRecord::getTextValue, PropertyRecord::setTextValue);
                    component = field;
                }
                break;
            default:
                throw new IllegalArgumentException("Property type is not supported");
        }
        component.setWidth(100, Unit.PERCENTAGE);
        component.focus();
        return component;
    }

    @Override
    protected void executeAction() {
        if (binder.validate().isOk()) {
            UpdatePropertyRequest request = buildRequest();
            BackgroundOperations.callApi("Saving setting", settingsApiClient.update(request), t -> {
                Notifications.trayNotification("Setting saved");
                close();
            }, Notifications::errorNotification);
        }
    }

    private UpdatePropertyRequest buildRequest() {
        UpdatePropertyRequest request = new UpdatePropertyRequest();
        request.setName(propertyRecord.getName());
        request.setTextValue(propertyRecord.getTextValue());
        request.setBooleanValue(propertyRecord.getBooleanValue());
        request.setDateTimeValue(propertyRecord.getDateTimeValue());
        if (propertyRecord.getDateValue() != null) {
            request.setDateValue(propertyRecord.getDateValue());
        }
        request.setNumberValue(propertyRecord.getNumberValue());
        request.setDecimalValue(propertyRecord.getDecimalValue());
        return request;
    }
}
