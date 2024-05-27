package fintech.bo.components.product;

import com.vaadin.ui.Component;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.ProductsApiClient;
import fintech.bo.api.model.product.UpdateProductSettingsRequest;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.lending.tables.records.ProductRecord;

public class EditProductSettingsDialog extends ActionDialog {

    private ProductsApiClient productsApiClient;
    private ProductRecord productRecord;
    private TextArea field;

    public EditProductSettingsDialog(ProductsApiClient productsApiClient, ProductRecord productRecord) {
        super("Edit Product Settings", "Save");
        this.productsApiClient = productsApiClient;
        this.productRecord = productRecord;

        setDialogContent(buildForm());
        setWidth(600, Unit.PIXELS);
        fullHeight();
    }

    private Component buildForm() {
        field = new TextArea("", productRecord.getDefaultSettingsJson());
        field.setSizeFull();
        field.setWidth(100, Unit.PERCENTAGE);
        field.addStyleName(BackofficeTheme.TEXT_MONO);
        field.focus();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(field);
        return layout;
    }

    @Override
    protected void executeAction() {
        UpdateProductSettingsRequest request = new UpdateProductSettingsRequest();
        request.setProductId(productRecord.getId());
        request.setSettingsJson(field.getValue());
        request.setProductType(productRecord.getProductType());

        BackgroundOperations.callApi("Saving product settings", productsApiClient.updateSettings(request), t -> {
            Notifications.trayNotification("Product settings saved");
            close();
        }, Notifications::errorNotification);
    }

}
