package fintech.bo.components.cms;

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import fintech.bo.api.client.CmsApiClient;
import fintech.bo.api.model.cms.AddCmsItemRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import org.jooq.DSLContext;
import retrofit2.Call;

import static fintech.bo.db.jooq.cms.Tables.LOCALE;

public class AddCmsItemDialog extends ActionDialog {

    private final CmsApiClient apiClient;
    private final AddCmsItemRequest request;


    public AddCmsItemDialog(CmsApiClient apiClient, DSLContext db) {
        super("Add item", "Save");
        this.apiClient = apiClient;

        this.request = new AddCmsItemRequest();
        this.request.setType(CmsConstants.TYPE_NOTIFICATION);
        this.request.setLocale(db.selectFrom(LOCALE)
            .where(LOCALE.IS_DEFAULT)
            .fetchOne(LOCALE.LOCALE_));

        FormLayout layout = buildForm();
        setWidth(400, Unit.PIXELS);
        setDialogContent(layout);
    }

    private FormLayout buildForm() {
        Binder<AddCmsItemRequest> binder = new Binder<>(AddCmsItemRequest.class);
        binder.setBean(request);

        ComboBox<String> type = new ComboBox<>("Select type");
        type.setItems(CmsConstants.ALL_TYPES);
        type.setWidth(100, Unit.PERCENTAGE);
        type.setEmptySelectionAllowed(false);
        type.setTextInputAllowed(false);
        binder.bind(type, AddCmsItemRequest::getType, AddCmsItemRequest::setType);

        TextField key = new TextField("Key");
        key.setWidth(100, Unit.PERCENTAGE);
        binder.bind(key, AddCmsItemRequest::getKey, AddCmsItemRequest::setKey);

        TextField description = new TextField("Description");
        description.setWidth(100, Unit.PERCENTAGE);
        binder.bind(description, AddCmsItemRequest::getDescription, AddCmsItemRequest::setDescription);

        FormLayout formLayout = new FormLayout();
        formLayout.addComponents(type, key, description);
        return formLayout;
    }

    @Override
    protected void executeAction() {
        Call<Void> call = apiClient.addItem(this.request);
        BackgroundOperations.callApi("Adding item", call, v -> {
            Notifications.trayNotification("Item added");
            close();
        }, Notifications::errorNotification);
    }
}
