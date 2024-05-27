package fintech.bo.spain.alfa.attachments;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardFeatures;
import fintech.bo.components.views.StandardScopes;

public class NewIdentificationDocumentDialog extends ActionDialog {

    private Long clientId;

    private NewIdentificationDocumentForm form;

    public NewIdentificationDocumentDialog(String caption, Long clientId) {
        super(caption, "Save");
        this.clientId = clientId;
        setDialogContent(content());
        setModal(true);
        setWidth(500, Unit.PIXELS);
    }

    private Component content() {
        form = new NewIdentificationDocumentForm(
            new BoComponentContext()
                .withScope(StandardScopes.SCOPE_CLIENT, clientId)
                .withFeature(StandardFeatures.FEATURE_VERTICAL_VIEW),
            true
        );

        form.setSuccessCallback(v -> close());

        center();

        VerticalLayout layout = new VerticalLayout();
        layout.addComponents(form);
        return layout;
    }

    @Override
    protected void executeAction() {
        form.submit();
    }
}
