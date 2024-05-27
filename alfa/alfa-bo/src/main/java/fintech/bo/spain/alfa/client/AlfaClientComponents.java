package fintech.bo.spain.alfa.client;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.WebitelButton;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.ClientQueries;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.client.repository.ClientRepository;
import fintech.bo.components.dialogs.InfoDialog;
import fintech.bo.components.risk.checklist.ChecklistQueries;
import fintech.bo.components.webitel.api.WebitelApiClient;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Stream;

import static fintech.bo.spain.alfa.client.AlfaChecklistQueries.CHECKLIST_TYPE_DNI;
import static fintech.bo.spain.alfa.client.AlfaChecklistQueries.CHECKLIST_TYPE_EMAIL;
import static fintech.bo.spain.alfa.client.AlfaChecklistQueries.CHECKLIST_TYPE_PHONE;

@Component
public class AlfaClientComponents extends ClientComponents {

    private WebitelApiClient webitelApiClient;
    private AlfaChecklistQueries alfaChecklistQueries;
    private ChecklistQueries checklistQueries;

    protected AlfaClientComponents(ClientRepository clientRepository, ClientQueries clientQueries,
                                   @Value("${whatsapp.url:https://wa.me/}") String whatsAppUrl,
                                   AlfaChecklistQueries alfaChecklistQueries,
                                   ChecklistQueries checklistQueries,
                                   WebitelApiClient webitelApiClient) {
        super(clientRepository, clientQueries, whatsAppUrl);
        this.webitelApiClient = webitelApiClient;
        this.alfaChecklistQueries = alfaChecklistQueries;
        this.checklistQueries = checklistQueries;
    }

    @Override
    public void addCustomDataBefore(PropertyLayout layout, ClientDTO client) {
        if (alfaChecklistQueries.isBlacklisted(client)) {
            Label label = new Label("Client is blocked");
            label.addStyleName(BackofficeTheme.TEXT_DANGER);
            label.addStyleName(ValoTheme.LABEL_BOLD);
            HorizontalLayout hr = new HorizontalLayout();
            hr.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
            hr.setSpacing(true);
            hr.addComponents(label, actionButton(VaadinIcons.INFO_CIRCLE, (event) -> {
                InfoDialog dlg = new InfoDialog("Blacklist data", blacklistDataComponent(client));
                dlg.setWidth(800, Unit.PIXELS);
                dlg.setHeight(250, Unit.PIXELS);
                UI.getCurrent().addWindow(dlg);
            }, true));
            layout.addComponentsAndExpand(hr);
        }
    }

    @Override
    protected com.vaadin.ui.Component[] mobilePhoneComponents(Long clientId, String phone) {
        com.vaadin.ui.Component[] components = super.mobilePhoneComponents(clientId, phone);
        return ArrayUtils.insert(0, components, webitelComponent(phone));
    }

    @Override
    protected com.vaadin.ui.Component[] additionalPhoneComponents(Long clientId, String phone) {
        com.vaadin.ui.Component[] components = super.additionalPhoneComponents(clientId, phone);
        return ArrayUtils.insert(0, components, webitelComponent(phone));
    }

    private com.vaadin.ui.Component webitelComponent(String phone) {
        return new WebitelButton(webitelApiClient, phone);
    }

    private com.vaadin.ui.Component blacklistDataComponent(ClientDTO client) {
        VerticalLayout result = new VerticalLayout();
        Stream.of(
            checklistQueries.findByTypeAndValue1(CHECKLIST_TYPE_DNI, client.getDocumentNumber()),
            checklistQueries.findByTypeAndValue1(CHECKLIST_TYPE_EMAIL, client.getEmail()),
            checklistQueries.findByTypeAndValue1(CHECKLIST_TYPE_PHONE, client.getPhone())
        ).filter(Optional::isPresent)
            .map(r -> String.format("%s (%s). Comment: %s", r.get().getType(), r.get().getValue1(), r.get().getComment()))
            .map(Label::new)
            .forEach(result::addComponent);
        return result;
    }
}
