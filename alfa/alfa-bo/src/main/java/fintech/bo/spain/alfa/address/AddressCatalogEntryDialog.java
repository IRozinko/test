package fintech.bo.spain.alfa.address;

import com.vaadin.data.Binder;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import fintech.bo.api.model.address.AddressCatalogEntry;
import fintech.bo.components.dialogs.ActionDialog;

class AddressCatalogEntryDialog extends ActionDialog {

    private Binder<AddressCatalogEntry> binder = new Binder<>();

    AddressCatalogEntryDialog(String caption, AddressCatalogEntry request) {
        super(caption, "Save");

        TextArea postalCode = new TextArea("Postal Code");
        postalCode.setWidth(100, Unit.PERCENTAGE);
        postalCode.setRows(2);
        postalCode.setRequiredIndicatorVisible(true);

        TextArea city = new TextArea("City");
        city.setWidth(100, Unit.PERCENTAGE);
        city.setRows(2);
        city.setRequiredIndicatorVisible(true);

        TextArea province = new TextArea("Province");
        province.setWidth(100, Unit.PERCENTAGE);
        province.setRows(2);
        province.setRequiredIndicatorVisible(true);

        TextArea state = new TextArea("State");
        state.setWidth(100, Unit.PERCENTAGE);
        state.setRows(2);
        state.setRequiredIndicatorVisible(true);

        binder.bind(postalCode, AddressCatalogEntry::getPostalCode, AddressCatalogEntry::setPostalCode);
        binder.bind(city, AddressCatalogEntry::getCity, AddressCatalogEntry::setCity);
        binder.bind(province, AddressCatalogEntry::getProvince, AddressCatalogEntry::setProvince);
        binder.bind(state, AddressCatalogEntry::getState, AddressCatalogEntry::setState);
        binder.setBean(request);

        FormLayout layout = new FormLayout();
        layout.addComponents(postalCode, city, province, state);
        setDialogContent(layout);
        setWidth(500, Unit.PIXELS);
    }

    AddressCatalogEntry getEntry() {
        return binder.getBean();
    }
}
