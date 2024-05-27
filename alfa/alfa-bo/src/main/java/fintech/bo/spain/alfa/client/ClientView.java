package fintech.bo.spain.alfa.client;

import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import fintech.bo.api.client.ActivityApiClient;
import fintech.bo.api.client.AttachmentApiClient;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.client.InitiateChangingBankAccountRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.AbstractBackofficeUI;
import fintech.bo.components.TabHelper;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.client.AbstractClientView;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.client.repository.ClientRepository;
import fintech.bo.components.dialogs.ConfirmDialog;
import fintech.bo.components.emails.EmailLogTab;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.loan.promocodes.PromoCodesComponents;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.sms.SmsLogTab;
import fintech.bo.components.views.BoComponentDiscovery;
import fintech.bo.db.jooq.crm.tables.records.ClientAddressRecord;
import fintech.bo.spain.asnef.AsnefComponents;
import fintech.bo.spain.alfa.AlfaBoConstants;
import fintech.bo.spain.alfa.address.AddressCatalogQueries;
import fintech.bo.spain.alfa.api.ClientApiClient;
import fintech.bo.spain.alfa.api.MarketingConsentApiClient;
import fintech.bo.spain.alfa.api.PhoneContactApiClient;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.bo.spain.alfa.attachments.NewIdentificationDocumentDialog;
import fintech.bo.spain.alfa.attachments.AlfaAttachmentTab;
import fintech.bo.spain.alfa.attachments.UploadAlfaAttachmentDialog;
import fintech.bo.spain.alfa.client.phone.PhoneContactTab;
import fintech.bo.spain.alfa.marketing.MarketingConsentTab;
import fintech.spain.alfa.bo.model.ClientWebLoginRequest;
import fintech.spain.alfa.bo.model.ClientWebLoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import java.util.Optional;

@Slf4j
@SpringView(name = AbstractClientView.NAME)
public class ClientView extends AbstractClientView {

    @Autowired
    private AlfaApiClient alfaApiClient;

    @Autowired
    private ClientApiClient clientApiClient;

    @Autowired
    private AddressCatalogQueries addressCatalogQueries;

    @Autowired
    private AsnefComponents asnefComponents;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private FileApiClient fileApiClient;

    @Autowired
    private AttachmentApiClient attachmentApiClient;

    @Autowired
    private MarketingConsentApiClient marketingConsentApiClient;

    @Autowired
    private ActivityApiClient activityApiClient;

    @Autowired
    private PhoneContactApiClient phoneContactApiClient;

    @Autowired
    private PromoCodesComponents promoCodesComponents;

    @Autowired
    private AlfaChecklistQueries alfaChecklistQueries;

    @Autowired
    private BoComponentDiscovery componentDiscovery;

    public ClientView(AlfaClientComponents clientComponents) {
        super(clientComponents);
    }

    @Override
    protected void addCustomActions(BusinessObjectLayout layout) {

        ClientDTO client = clientRepository.getRequired(clientId);

        if (LoginService.hasPermission(BackofficePermissions.CLIENT_EDIT)) {
            layout.addActionMenuItem("Upload file", e -> uploadFile());
        }
        if (LoginService.hasPermission(BackofficePermissions.CLIENT_EDIT)) {
            layout.addActionMenuItem("Edit personal data", (event) -> editClient());
        }
        if (LoginService.hasPermission(BackofficePermissions.CLIENT_EDIT)) {
            layout.addActionMenuItem("Add address", (event) -> addAddress());
        }
        if (LoginService.hasPermission(BackofficePermissions.CLIENT_WEB_LOGIN)) {
            layout.addActionMenuItem("View client web profile", (event) -> viewWebProfile());
        }
        if (LoginService.hasPermission(BackofficePermissions.CLIENT_SOFT_DELETE)) {
            layout.addActionMenuItem("Soft delete", (event) -> softDeleteClient());
        }
        if (LoginService.hasPermission(BackofficePermissions.CLIENT_HARD_DELETE)) {
            layout.addActionMenuItem("Hard delete", (event) -> hardDeleteClient());
        }

        boolean blacklisted = alfaChecklistQueries.isBlacklisted(client);
        if (LoginService.hasPermission(BackofficePermissions.CLIENT_EDIT) && !blacklisted) {
            layout.addActionMenuItem("Blacklist customer", (event) -> blacklistCustomer());
        }
        if (LoginService.hasPermission(BackofficePermissions.CLIENT_EDIT) && blacklisted) {
            layout.addActionMenuItem("Unblacklist customer", (event) -> unBlacklistCustomer());
        }
    }

    @Override
    protected void buildTabs(ClientDTO client, BusinessObjectLayout layout) {
//        layout.addTab("Applications", () -> applications(client));
        layout.addTab("Debts", () -> loans(client));
        layout.addTab("Payments", () -> payments(client));
        TabHelper.addIfAllowed(layout, new AttachmentsTab("Attachments", client, attachmentsComponents));
//        layout.addTab("Tasks", () -> tasks(client));
        TabHelper.addIfAllowed(layout, new AddressTab("Addresses", client, db));
        TabHelper.addIfAllowed(layout, new SmsLogTab("SMS", client, smsComponents));
        TabHelper.addIfAllowed(layout, new EmailLogTab("Email", client, emailsComponents));
//        layout.addTab("Workflows", () -> workflows(client));
//        layout.addTab("Disbursements", () -> disbursements(client));
        TabHelper.addIfAllowed(layout, new AdvancedTab("Advanced", client, componentDiscovery));
//        layout.addTab("Marketing", this::marketing);
        layout.addTab("Promo codes", () -> promoCodes(client));
        TabHelper.addIfAllowed(layout, new AlfaAttachmentTab("ID Docs", client, db, fileApiClient, alfaApiClient));
//        TabHelper.addIfAllowed(layout, new BankAccountTab("Bank accounts", client, db));

        if (LoginService.hasPermission(BackofficePermissions.CLIENT_PHONES_TAB_VIEW, BackofficePermissions.CLIENT_PHONES_TAB_VIEW_AND_EDIT)) {
            layout.addTab("Phones", this::phones);
        }

//        layout.addTab("Asnef", () -> asnefComponents.asnefTab(this.clientId));
        layout.addTab("Transactions", () -> transactions(client));
//        layout.addTab("Accounting", () -> accounting(client));
//        layout.addTab("Discounts", () -> discounts(client));
//        TabHelper.addIfAllowed(layout, new AttributesTab("Attributes", client, db));
        layout.addTab("Activity", () -> activity(client));
    }

    private Component promoCodes(ClientDTO client) {
        return promoCodesComponents.clientPromoCodesGrid(client);
    }

    private Component marketing() {
        return new MarketingConsentTab(clientId, db, marketingConsentApiClient, clientRepository, activityApiClient);
    }

    private void uploadFile() {
        UploadAlfaAttachmentDialog dialog = new UploadAlfaAttachmentDialog("Upload file", clientId, fileApiClient, attachmentApiClient, this::openIdentificationDocumentPopup);
        dialog.addCloseListener((e) -> refresh());
        getUI().addWindow(dialog);
    }

    private void openIdentificationDocumentPopup(String attachmentType) {
        if (AlfaBoConstants.ATTACHMENT_TYPE_ID_DOCUMENT.equals(attachmentType)) {
            NewIdentificationDocumentDialog dialog = new NewIdentificationDocumentDialog("New Identification Document", clientId);
            getUI().addWindow(dialog);
        }
    }

    private void editClient() {
        ClientDTO client = clientRepository.getRequired(clientId);
        EditClientDataDialog dialog = new EditClientDataDialog(client, alfaApiClient);
        dialog.addCloseListener((e) -> refresh());
        getUI().addWindow(dialog);
    }

    private void blacklistCustomer() {
        ClientDTO client = clientRepository.getRequired(clientId);
        BlacklistClientDialog dialog = new BlacklistClientDialog(client, alfaApiClient);
        dialog.addCloseListener((e) -> refresh());
        getUI().addWindow(dialog);
    }

    private void unBlacklistCustomer() {
        ConfirmDialog confirm = new ConfirmDialog("Unblacklist client? Blacklist data will be removed!", (Button.ClickListener) event -> {
            Call<Void> call = alfaApiClient.unBlacklistClient(new IdRequest(clientId));
            BackgroundOperations.callApi("Unblacklisting client", call, t -> {
                Notifications.trayNotification("Client unblacklisted");
                refresh();
            }, Notifications::errorNotification);
        });
        getUI().addWindow(confirm);
    }

    private void viewWebProfile() {
        ClientWebLoginRequest request = new ClientWebLoginRequest();
        request.setClientId(this.clientId);
        Call<ClientWebLoginResponse> call = alfaApiClient.clientWebLogin(request);
        BackgroundOperations.callApi("Logging in", call,
            response -> getUI().getPage().open(response.getUrl(), "_blank"),
            Notifications::errorNotification);
    }

    private void initiateChangingBankAccount() {
        InitiateChangingBankAccountRequest request = new InitiateChangingBankAccountRequest();
        request.setClientId(this.clientId);

        Call<Void> call = clientApiClient.initiateChangingBankAccount(request);
        BackgroundOperations.callApi("Initiating Change Bank Account", call, t -> {
            Notifications.trayNotification("Initiating Change Bank Account Done");
            refresh();
        }, Notifications::errorNotification);
    }

    private void softDeleteClient() {
        ConfirmDialog confirm = new ConfirmDialog("Delete client? Data will be obfuscated!", (Button.ClickListener) event -> {
            Call<Void> call = alfaApiClient.softDeleteClient(new IdRequest(clientId));
            BackgroundOperations.callApi("Deleting client", call, t -> {
                Notifications.trayNotification("Client deleted");
                refresh();
            }, Notifications::errorNotification);
        });
        getUI().addWindow(confirm);
    }

    private void hardDeleteClient() {
        ConfirmDialog confirm = new ConfirmDialog("Delete client permanently?!", (Button.ClickListener) event -> {
            Call<Void> call = alfaApiClient.hardDeleteClient(new IdRequest(clientId));
            BackgroundOperations.callApi("Deleting client", call, t -> {
                Notifications.trayNotification("Client permanently deleted!");
                ((AbstractBackofficeUI) UI.getCurrent()).getTabSheetNavigator().closeCurrentTab();
            }, Notifications::errorNotification);
        });
        getUI().addWindow(confirm);
    }

    private void addAddress() {
        Optional<ClientAddressRecord> clientAddress = clientQueries.findPrimaryAddress(clientId);
        EditClientAddressDialog dialog = new EditClientAddressDialog(clientId, clientAddress, alfaApiClient, addressCatalogQueries);
        dialog.addCloseListener((e) -> refresh());
        getUI().addWindow(dialog);
    }

    private Component phones() {
        return new PhoneContactTab(clientId, db, phoneContactApiClient);
    }

}
