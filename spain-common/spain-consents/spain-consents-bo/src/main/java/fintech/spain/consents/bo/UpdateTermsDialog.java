package fintech.spain.consents.bo;

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.spain.consents.bo.api.ConsentApiClient;
import fintech.spain.consents.bo.api.model.UpdateTermsRequest;
import org.jooq.DSLContext;

import java.util.List;
import java.util.stream.Collectors;

import static fintech.spain.consents.db.jooq.Tables.TERMS;

public class UpdateTermsDialog extends ActionDialog {

    private final ConsentApiClient consentApiClient;
    private final DSLContext db;
    private final Binder<UpdateTermsRequest> binder;

    public UpdateTermsDialog() {
        super("Update terms", "Update");
        consentApiClient = ApiAccessor.gI().get(ConsentApiClient.class);
        db = ApiAccessor.gI().get(DSLContext.class);
        binder = new Binder<>();
        binder.setBean(new UpdateTermsRequest());

        setDialogContent(createContent());
        setWidth(800, Unit.PIXELS);
        fullHeight();
    }

    private Component createContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        ComboBox<String> consentType = new ComboBox<>("Consent type");
        consentType.setWidth(300, Unit.PIXELS);
        consentType.setEmptySelectionAllowed(false);
        consentType.setTextInputAllowed(false);
        consentType.setPlaceholder("Select consent type...");
        consentType.setItems(getConsentTypes());
        binder.forField(consentType)
            .asRequired()
            .bind(UpdateTermsRequest::getName, UpdateTermsRequest::setName);

        TextField version = new TextField("Version");
        version.setWidth(250, Unit.PIXELS);
        binder.forField(version)
            .asRequired()
            .bind(UpdateTermsRequest::getVersion, UpdateTermsRequest::setVersion);

        TextArea terms = new TextArea("Terms");
        terms.setWidth(100, Unit.PERCENTAGE);
        binder.forField(terms)
            .asRequired()
            .bind(UpdateTermsRequest::getText, UpdateTermsRequest::setText);

        layout.addComponents(consentType, version);
        layout.addComponentsAndExpand(terms);
        return layout;
    }

    private List<String> getConsentTypes() {
        return db.selectDistinct(TERMS.NAME).from(TERMS).stream()
            .map(r -> r.get(TERMS.NAME))
            .collect(Collectors.toList());
    }

    @Override
    protected void executeAction() {
        if (binder.validate().isOk()) {
            BackgroundOperations.callApi("Updating terms", consentApiClient.updateTerms(binder.getBean()), t -> {
                Notifications.trayNotification("Updated");
                close();
            }, Notifications::errorNotification);
        }
    }

}
