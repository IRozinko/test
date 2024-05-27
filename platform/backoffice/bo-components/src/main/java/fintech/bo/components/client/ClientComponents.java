package fintech.bo.components.client;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.components.DateUtils;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.client.history.ClientDataHistory;
import fintech.bo.components.client.history.ClientDataHistoryDialog;
import fintech.bo.components.client.history.ClientDataHistoryRequest;
import fintech.bo.components.client.repository.ClientRepository;
import fintech.bo.db.jooq.crm.tables.records.PhoneContactRecord;
import org.apache.commons.lang3.text.WordUtils;
import org.jooq.Record;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.MoreObjects.firstNonNull;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

public abstract class ClientComponents {

    private final ClientRepository clientRepository;
    protected final ClientQueries clientQueries;

    private final String whatsAppUrl;

    protected ClientComponents(ClientRepository clientRepository, ClientQueries clientQueries, String whatsAppUrl) {
        this.clientRepository = clientRepository;
        this.clientQueries = clientQueries;
        this.whatsAppUrl = whatsAppUrl;
    }

    public ComboBox<ClientDTO> clientsComboBox() {
        ComboBox<ClientDTO> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Select client...");
        comboBox.setPageLength(20);
        comboBox.setDataProvider(clientRepository);
        comboBox.setPopupWidth("600px");
        comboBox.setItemCaptionGenerator(item -> String.format("%s | %s | %s | %s",
            item.getClientNumber(),
            Joiner.on(" ").join(
                capitalizeFully(firstNonNull(item.getFirstName(), "")),
                capitalizeFully(firstNonNull(item.getLastName(), "")),
                capitalizeFully(firstNonNull(item.getSecondLastName(), ""))
            ),
            lowerCase(firstNonNull(item.getEmail(), "")),
            firstNonNull(item.getPhone(), "")));
        return comboBox;
    }

    public static String clientLink(Long clientId) {
        return AbstractClientView.NAME + "/" + clientId;
    }

    public static String firstAndLastName(ClientDTO client) {
        return String.format("%s %s", MoreObjects.firstNonNull(WordUtils.capitalizeFully(client.getFirstName()), ""), MoreObjects.firstNonNull(WordUtils.capitalizeFully(client.getLastName()), "-"));
    }

    public static Button actionButton(VaadinIcons icon, Button.ClickListener listener,
                                      boolean visible) {
        Button button = new Button(icon);
        button.setStyleName(ValoTheme.BUTTON_LINK);
        button.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        button.addClickListener(listener);
        button.setVisible(visible);
        return button;
    }

    public PropertyLayout clientInfo(Long clientId) {
        return clientInfo(clientRepository.getRequired(clientId));
    }

    public PropertyLayout clientInfo(ClientDTO client) {
        PropertyLayout layout = new PropertyLayout("Client");
        addCustomDataBefore(layout, client);
        layout.addLink("Number", client.getClientNumber(), ClientComponents.clientLink(client.getId()));
        layout.add("First name", client.getFirstName());
        layout.add("Last name", client.getLastName());
        layout.add("Second last name", client.getSecondLastName());
        addEmailComponent(layout, client);
        Optional<PhoneContactRecord> primaryPhone = clientQueries.findPrimaryPhone(client.getId());
        String phone = primaryPhone.map(r -> r.getCountryCode() + r.getLocalNumber()).orElseGet(client::getPhone);
        if (client.isDeleted()) {
            layout.add("Mobile Phone", phone);
        } else {
            layout.add("Mobile Phone", phone, mobilePhoneComponents(client.getId(), phone));
        }

        Optional.ofNullable(client.getAdditionalPhone()).ifPresent(
            p -> {
                if (client.isDeleted()) {
                    layout.add("Additional phone", p);
                } else {
                    layout.add("Additional phone", p, additionalPhoneComponents(client.getId(), p));
                }
            }
        );
        layout.add("Document Number", client.getDocumentNumber());
        layout.add("Account Number", client.getAccountNumber());
        layout.add("Date of birth", client.getDateOfBirth());
        layout.add("Age", DateUtils.yearsFromNow(client.getDateOfBirth()));
        layout.add("Gender", client.getGender());
        layout.add("Accept marketing", client.getAcceptMarketing());
        layout.add("Block communication", client.getBlockCommunication());
        layout.add("Excluded from ASNEF", client.getExcludedFromAsnef());
        layout.add("Created at", client.getCreatedAt());
        BigDecimal overpayment = clientQueries.getClientOverpaymentAvailable(client.getId());
        layout.add("Overpayment available", overpayment);
        layout.add("Credit limit", clientQueries.getCreditLimit(client.getId()));
        return layout;
    }

    public void addCustomDataBefore(PropertyLayout layout, ClientDTO client) {

    }

    protected void addEmailComponent(PropertyLayout layout, ClientDTO client) {
        if (client.isDeleted()) {
            layout.add("Email", client.getEmail());
        } else {
            layout.add("Email", client.getEmail(),
                clientDataHistoryComponent(client.getId(), ClientDataHistoryRequest.EMAIL)
            );
        }
    }

    protected Component[] mobilePhoneComponents(Long clientId, String phone) {
        return new Component[]{
            whatsAppComponent(phone),
            clientDataHistoryComponent(clientId, ClientDataHistoryRequest.MOBILE_PHONE)
        };
    }

    protected Component[] additionalPhoneComponents(Long clientId, String phone) {
        return new Component[]{
            clientDataHistoryComponent(clientId, ClientDataHistoryRequest.OTHER_PHONE)
        };
    }

    public PropertyLayout clientInfoSimple(Long clientId) {
        return clientInfoSimple(clientId, false);
    }

    public PropertyLayout clientInfoSimple(Long clientId, boolean showMobilePhoneActions) {
        return clientInfoSimple(clientRepository.getRequired(clientId), showMobilePhoneActions);
    }

    public PropertyLayout clientInfoSimple(ClientDTO client, boolean showMobilePhoneActions) {
        Optional<PhoneContactRecord> primaryPhone = clientQueries.findPrimaryPhone(client.getId());
        String phone = primaryPhone.map(r -> r.getCountryCode() + r.getLocalNumber()).orElseGet(client::getPhone);

        PropertyLayout layout = new PropertyLayout("Client");
        layout.addLink("Number", client.getClientNumber(), ClientComponents.clientLink(client.getId()));
        layout.add("First name", client.getFirstName());
        layout.add("Last name", client.getLastName());
        layout.add("Second last name", client.getSecondLastName());
        layout.add("Email", client.getEmail());
        if (showMobilePhoneActions) {
            layout.add("Mobile phone", phone, mobilePhoneComponents(client.getId(), phone));
            Optional.ofNullable(client.getAdditionalPhone()).ifPresent(p -> layout.add("Additional phone", p,
                additionalPhoneComponents(client.getId(), p)));
        } else {
            layout.add("Mobile phone", phone);
            Optional.ofNullable(client.getAdditionalPhone()).ifPresent(p -> layout.add("Additional phone", p));
        }

        layout.add("Document Number", client.getDocumentNumber());
        layout.add("Paid loans", client.getPaidLoans());
        return layout;
    }

    private Component clientDataHistoryComponent(Long clientId, ClientDataHistoryRequest<? extends Record> req) {
        List<ClientDataHistory> records = clientQueries.findDataHistory(clientId, req);
        boolean buttonVisible = records.size() > 1;
        return ClientComponents.actionButton(VaadinIcons.TIME_BACKWARD, showHistory(records), buttonVisible);
    }

    private Component whatsAppComponent(String phone) {
        return ClientComponents.actionButton(VaadinIcons.COMMENT_ELLIPSIS,
            (e) -> UI.getCurrent().getPage().open(whatsAppUrl + phone, "WhatsApp", true), true);
    }

    private Button.ClickListener showHistory(List<ClientDataHistory> records) {
        return event -> {
            ClientDataHistoryDialog dialog = new ClientDataHistoryDialog(records);
            UI.getCurrent().addWindow(dialog);
        };
    }

    public ClientQueries getClientQueries() {
        return clientQueries;
    }
}
