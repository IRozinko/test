package fintech.bo.spain.alfa.attachments;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.dialogs.InfoDialog;
import fintech.bo.db.jooq.alfa.tables.records.IdentificationDocumentRecord;

public class IdentificationDocumentDialog {

    private final Panel scroll;

    public IdentificationDocumentDialog(IdentificationDocumentRecord record) {
        PropertyLayout props = new PropertyLayout("Identification Document");
        props.add("Document Type", record.getDocumentType());
        props.add("Document Number", record.getDocumentNumber());
        props.add("Expiration Date", record.getExpirationDate());
        props.addSpacer();
        props.add("Surname 1", record.getSurname_1());
        props.add("Surname 2", record.getSurname_2());
        props.add("Name", record.getName());
        props.add("Gender", record.getGender());
        props.add("Nationality", record.getNationality());
        props.add("Date of Birth", record.getDateOfBirth());
        props.add("Place of Birth", record.getPlaceOfBirth());
        props.addSpacer();
        props.add("Street", record.getStreet());
        props.add("House and Apartment number", record.getHouse());
        props.add("City", record.getCity());
        props.add("Province", record.getProvince());

        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(props);
        scroll = new Panel();
        scroll.setContent(vl);
    }

    public InfoDialog view() {
        InfoDialog dialog = Dialogs.preview("Identification Document", scroll);
        dialog.setWidth(700, Sizeable.Unit.PIXELS);
        dialog.setHeight(700, Sizeable.Unit.PIXELS);
        return dialog;
    }

}
