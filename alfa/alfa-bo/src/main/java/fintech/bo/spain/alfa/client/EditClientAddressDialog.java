package fintech.bo.spain.alfa.client;

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.crm.tables.records.ClientAddressRecord;
import fintech.bo.spain.alfa.address.AddressCatalogQueries;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.spain.alfa.bo.model.AddClientAddressRequest;

import java.util.List;
import java.util.Optional;

public class EditClientAddressDialog extends ActionDialog {
    private final Long clientId;

    private final Optional<ClientAddressRecord> clientAddress;

    private final AlfaApiClient clientApi;

    private final AddressCatalogQueries addressCatalogQueries;

    private Binder<AddClientAddressRequest> binder;

    private AddressCatalogQueries.AddressQuery addressQuery;

    public EditClientAddressDialog(Long clientId, Optional<ClientAddressRecord> clientAddress,
                                   AlfaApiClient spainApiClient, AddressCatalogQueries addressCatalogQueries) {
        super("Add client address", "Save");

        this.clientId = clientId;
        this.clientAddress = clientAddress;
        this.clientApi = spainApiClient;
        this.addressCatalogQueries = addressCatalogQueries;
        this.addressQuery = new AddressCatalogQueries.AddressQuery();

        setDialogContent(form());
        setWidth(600, Unit.PIXELS);
    }

    @Override
    protected void executeAction() {
        BackgroundOperations.callApi("Adding address", clientApi.addClientAddress(binder.getBean()), t -> {
            Notifications.trayNotification("Added");
            close();
        }, Notifications::errorNotification);
    }

    private Component form() {
        AddClientAddressRequest clientData = new AddClientAddressRequest();
        clientData.setClientId(clientId);
        clientData.setType(AddressConstants.ADDRESS_TYPE_ACTUAL);

        FormLayout form = new FormLayout();
        form.setMargin(true);

        TextField type = new TextField("Type");
        type.setWidth(100, Unit.PERCENTAGE);
        type.setEnabled(false);
        form.addComponent(type);

        ComboBox<String> city = new ComboBox<>("City");
        city.setItems(addressCatalogQueries.listCities(addressQuery));
        city.setWidth(100, Unit.PERCENTAGE);

        ComboBox<String> province = new ComboBox<>("Province");
        province.setItems(addressCatalogQueries.listProvinces(addressQuery));
        province.setWidth(100, Unit.PERCENTAGE);

        ComboBox<String> postalCode = new ComboBox<>("Postal code");
        postalCode.setItems(addressCatalogQueries.listPostalCodes());
        postalCode.setWidth(100, Unit.PERCENTAGE);
        postalCode.addValueChangeListener((event) -> {
            addressQuery = new AddressCatalogQueries.AddressQuery();
            addressQuery.setPostalCode(event.getValue());

            List<String> cities = addressCatalogQueries.listCities(addressQuery);
            city.setItems(cities);
            city.setSelectedItem(cities.size() == 1 ? cities.get(0) : "");

            List<String> provinces = addressCatalogQueries.listProvinces(addressQuery);
            province.setItems(provinces);
            province.setSelectedItem(provinces.size() == 1 ? provinces.get(0) : "");
        });
        city.addValueChangeListener((event) -> {
            addressQuery.setCity(event.getValue());
            addressQuery.setProvince(null);
            List<String> provinces = addressCatalogQueries.listProvinces(addressQuery);
            province.setItems(provinces);
            province.setSelectedItem(provinces.size() == 1 ? provinces.get(0) : "");
        });


        form.addComponent(postalCode);
        form.addComponent(city);
        form.addComponent(province);

        TextField street = new TextField("Street");
        street.setWidth(100, Unit.PERCENTAGE);
        form.addComponent(street);

        TextField houseNumber = new TextField("House number");
        houseNumber.setWidth(100, Unit.PERCENTAGE);
        form.addComponent(houseNumber);

        ComboBox<String> housingTenure = new ComboBox<>("Housing tenure");
        housingTenure.setItems(AddressConstants.TENURE_TYPES);
        housingTenure.setWidth(100, Unit.PERCENTAGE);
        form.addComponent(housingTenure);

        if (clientAddress.isPresent()) {
            ClientAddressRecord currentAddress = clientAddress.get();
            clientData.setType(currentAddress.getType());
            clientData.setPostalCode(currentAddress.getPostalCode());
            clientData.setCity(currentAddress.getCity());
            clientData.setProvince(currentAddress.getProvince());
            clientData.setStreet(currentAddress.getStreet());
            clientData.setHouseNumber(currentAddress.getHouseNumber());
            clientData.setHousingTenure(currentAddress.getHousingTenure());
        }

        binder = new Binder<>(AddClientAddressRequest.class);
        binder.setBean(clientData);
        binder.bind(type, "type");
        binder.bind(postalCode, "postalCode");
        binder.bind(city, "city");
        binder.bind(province, "province");
        binder.bind(street, "street");
        binder.bind(houseNumber, "houseNumber");
        binder.bind(housingTenure, "housingTenure");

        return form;
    }


}
