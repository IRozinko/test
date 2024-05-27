package fintech.bo.spain.alfa.attachments;

import com.vaadin.data.Binder;
import com.vaadin.server.UserError;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fintech.TimeMachine;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardFeatures;
import fintech.bo.components.views.StandardScopes;
import fintech.bo.db.jooq.crm.Tables;
import fintech.bo.db.jooq.crm.tables.records.ClientAttachmentRecord;
import fintech.bo.db.jooq.crm.tables.records.ClientRecord;
import fintech.bo.db.jooq.crm.tables.records.CountryRecord;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.spain.alfa.bo.model.SaveIdentificationDocumentRequest;
import org.jooq.DSLContext;
import org.jooq.Result;
import retrofit2.Call;

import java.util.List;
import java.util.function.Consumer;

import static fintech.bo.db.jooq.crm.tables.ClientAttachment.CLIENT_ATTACHMENT;

public class NewIdentificationDocumentForm extends CustomComponent {

    static final String NO_EXPIRATION_DATE = "No expiration date";
    static final String HAS_EXPIRATION_DATE = "Has expiration date";

    private final Binder<IdentificationDocumentModel> binder = new Binder<>();
    private final ComboBox<ClientAttachmentRecord> frontAttachment;
    private final ComboBox<ClientAttachmentRecord> backAttachment;
    private final ComboBox<SaveIdentificationDocumentRequest.DocumentType> documentType;
    private final DateField expirationDate;
    private final ComboBox<String> gender;
    private final RadioButtonGroup<String> expiryDateOptions;
    private final TextField docNumber;
    private final TextField name;
    private final TextField surname1;
    private final TextField surname2;
    private final ComboBox<CountryRecord> nationality;
    private final DateField dateOfBirth;
    private final ComboBox<CountryRecord> placeOfBirth;
    private final TextField street;
    private final TextField house;
    private final TextField city;
    private final TextField province;

    private Consumer<Void> successCallback;
    private BoComponentContext context;

    private final Long clientId;

    public NewIdentificationDocumentForm(BoComponentContext context) {
        this(context, false);
    }

    public NewIdentificationDocumentForm(BoComponentContext context, boolean notifyOnSave) {
        this.context = context;

        DSLContext db = ApiAccessor.gI().get(DSLContext.class);

        clientId = context.scope(StandardScopes.SCOPE_CLIENT)
            .orElseThrow(() -> new IllegalArgumentException("Client scope is required"));

        ClientRecord clientRecord = db.selectFrom(Tables.CLIENT)
            .where(Tables.CLIENT.ID.eq(clientId)).fetchOne();
        List<ClientAttachmentRecord> clientAttachments = db.selectFrom(CLIENT_ATTACHMENT)
            .where(CLIENT_ATTACHMENT.CLIENT_ID.eq(clientId))
            .fetchInto(ClientAttachmentRecord.class);
        Result<CountryRecord> countriesAndNationalities = db.selectFrom(Tables.COUNTRY).fetch();
        IdentificationDocumentModel model = new IdentificationDocumentModel();
        binder.setBean(model
            .setNotifyOnSave(notifyOnSave)
            .setNationality("Spanish")
            .setDocumentNumber(clientRecord.getDocumentNumber())
            .setGender(clientRecord.getGender())
            .setName(clientRecord.getFirstName())
            .setSurname1(clientRecord.getLastName())
            .setDateOfBirth(clientRecord.getDateOfBirth())
            .setTaskId(context.scope(StandardScopes.SCOPE_TASK).orElse(null))
            .setClientId(clientId));

        frontAttachment = new ComboBox<>("Select document 1");
        frontAttachment.setWidth(100, Unit.PERCENTAGE);
        frontAttachment.setItems(clientAttachments);
        frontAttachment.setItemCaptionGenerator(a -> String.format("%d - %s", a.getId(), a.getName()));
        frontAttachment.setEmptySelectionAllowed(false);
        binder.forField(frontAttachment)
            .asRequired()
            .bind(IdentificationDocumentModel::getFrontAttachment, IdentificationDocumentModel::setFrontAttachment);

        backAttachment = new ComboBox<>("Select document 2");
        backAttachment.setWidth(100, Unit.PERCENTAGE);
        backAttachment.setItems(clientAttachments);
        backAttachment.setItemCaptionGenerator(a -> String.format("%d - %s", a.getId(), a.getName()));
        backAttachment.setEmptySelectionAllowed(false);
        binder.forField(backAttachment)
            .asRequired()
            .bind(IdentificationDocumentModel::getBackAttachment, IdentificationDocumentModel::setBackAttachment);

        documentType = new ComboBox<>("Document Type");
        documentType.setWidth(50, Unit.PERCENTAGE);
        documentType.setItems(SaveIdentificationDocumentRequest.DocumentType.values());
        documentType.setEmptySelectionAllowed(false);
        documentType.setTextInputAllowed(false);
        documentType.setItemCaptionGenerator(SaveIdentificationDocumentRequest.DocumentType::getLabel);
        binder.forField(documentType)
            .asRequired()
            .bind(IdentificationDocumentModel::getDocumentType, IdentificationDocumentModel::setDocumentType);

        expiryDateOptions = new RadioButtonGroup<>();
        expiryDateOptions.setVisible(false);
        expiryDateOptions.setRequiredIndicatorVisible(true);
        expiryDateOptions.addStyleName(ValoTheme.OPTIONGROUP_SMALL);
        expiryDateOptions.setItems(NO_EXPIRATION_DATE, HAS_EXPIRATION_DATE);

        docNumber = new TextField("Doc Number");
        docNumber.setWidth(100, Unit.PERCENTAGE);
        binder.forField(docNumber)
            .asRequired()
            .bind(IdentificationDocumentModel::getDocumentNumber, IdentificationDocumentModel::setDocumentNumber);

        name = new TextField("Name");
        name.setWidth(100, Unit.PERCENTAGE);
        binder.forField(name)
            .asRequired()
            .bind(IdentificationDocumentModel::getName, IdentificationDocumentModel::setName);

        surname1 = new TextField("Surname 1");
        surname1.setWidth(100, Unit.PERCENTAGE);
        binder.forField(surname1)
            .asRequired()
            .bind(IdentificationDocumentModel::getSurname1, IdentificationDocumentModel::setSurname1);

        surname2 = new TextField("Surname 2");
        surname2.setWidth(100, Unit.PERCENTAGE);
        binder.forField(surname2)
            .bind(IdentificationDocumentModel::getSurname2, IdentificationDocumentModel::setSurname2);

        gender = new ComboBox<>("Gender");
        gender.setWidth(100, Unit.PERCENTAGE);
        gender.setItems("Male", "Female");
        gender.setTextInputAllowed(false);
        gender.setEmptySelectionAllowed(false);
        binder.forField(gender)
            .asRequired()
            .bind(IdentificationDocumentModel::getGender, IdentificationDocumentModel::setGender);

        nationality = new ComboBox<>("Nationality");
        nationality.setWidth(100, Unit.PERCENTAGE);
        nationality.setItems(countriesAndNationalities);
        nationality.setItemCaptionGenerator(CountryRecord::getNationalityDisplayName);
        binder.forField(nationality)
            .asRequired()
            .bind(request -> countriesAndNationalities.stream().filter(f -> f.getNationality().equals(request.getNationality())).findFirst().orElse(null),
                (request, countryRecord) -> request.setNationality(countryRecord.getNationality()));

        dateOfBirth = new DateField("Date of Birth");
        dateOfBirth.setWidth(100, Unit.PERCENTAGE);
        dateOfBirth.setDateFormat("yyyy-MM-dd");
        dateOfBirth.setRangeEnd(TimeMachine.today());
        binder.forField(dateOfBirth)
            .asRequired()
            .bind(IdentificationDocumentModel::getDateOfBirth, IdentificationDocumentModel::setDateOfBirth);

        expirationDate = new DateField("Expiration Date");
        expirationDate.setWidth(100, Unit.PERCENTAGE);
        expirationDate.setDateFormat("yyyy-MM-dd");
        binder.forField(expirationDate)
            .bind(IdentificationDocumentModel::getExpirationDate, IdentificationDocumentModel::setExpirationDate);

        street = new TextField("Street");
        street.setWidth(100, Unit.PERCENTAGE);
        binder.forField(street)
            .bind(IdentificationDocumentModel::getStreet, IdentificationDocumentModel::setStreet);

        house = new TextField("House and Apartment Number");
        house.setWidth(100, Unit.PERCENTAGE);
        binder.forField(house)
            .bind(IdentificationDocumentModel::getHouse, IdentificationDocumentModel::setHouse);

        city = new TextField("City");
        city.setWidth(100, Unit.PERCENTAGE);
        binder.forField(city)
            .bind(IdentificationDocumentModel::getCity, IdentificationDocumentModel::setCity);

        province = new TextField("Province");
        province.setWidth(100, Unit.PERCENTAGE);
        binder.forField(province)
            .bind(IdentificationDocumentModel::getProvince, IdentificationDocumentModel::setProvince);

        placeOfBirth = new ComboBox<>("Place of Birth");
        placeOfBirth.setWidth(100, Unit.PERCENTAGE);
        placeOfBirth.setItems(countriesAndNationalities);
        placeOfBirth.setItemCaptionGenerator(CountryRecord::getDisplayName);
        binder.forField(placeOfBirth)
            .bind(
                request -> countriesAndNationalities.stream().filter(f -> f.getName().equals(request.getPlaceOfBirth())).findFirst().orElse(null),
                (request, countryRecord) -> request.setPlaceOfBirth(countryRecord.getName())
            );


        documentType.addValueChangeListener(v -> {
            if (SaveIdentificationDocumentRequest.DocumentType.NIE.equals(v.getValue())) {
                expiryDateOptions.setVisible(true);
            } else {
                expiryDateOptions.setValue(null);
                expiryDateOptions.setVisible(false);
            }
            expirationDate.setRequiredIndicatorVisible(SaveIdentificationDocumentRequest.DocumentType.PASSPORT.equals(v.getValue()) || SaveIdentificationDocumentRequest.DocumentType.DNI.equals(v.getValue()));
            gender.setRequiredIndicatorVisible(SaveIdentificationDocumentRequest.DocumentType.PASSPORT.equals(v.getValue()));
        });

        expiryDateOptions.addValueChangeListener(v -> {
            expirationDate.setRequiredIndicatorVisible(HAS_EXPIRATION_DATE.equals(v.getValue()));
        });

        compose();
    }

    private void compose() {
        HorizontalLayout row1 = new HorizontalLayout();
        row1.setWidth(100, Unit.PERCENTAGE);
        row1.addComponents(docNumber, name);

        HorizontalLayout row2 = new HorizontalLayout();
        row2.setWidth(100, Unit.PERCENTAGE);
        row2.addComponents(surname1, surname2);

        HorizontalLayout row3 = new HorizontalLayout();
        row3.setWidth(100, Unit.PERCENTAGE);
        row3.addComponents(gender, nationality);

        HorizontalLayout row4 = new HorizontalLayout();
        row4.setWidth(100, Unit.PERCENTAGE);
        row4.addComponents(dateOfBirth, expirationDate);

        HorizontalLayout row5 = new HorizontalLayout();
        row5.setWidth(100, Unit.PERCENTAGE);
        row5.addComponents(street, house);

        HorizontalLayout row6 = new HorizontalLayout();
        row6.setWidth(100, Unit.PERCENTAGE);
        row6.addComponents(city, province);

        AbstractOrderedLayout compositionLayout;

        if (context.requiresFeature(StandardFeatures.FEATURE_VERTICAL_VIEW)) {
            compositionLayout = new VerticalLayout();
            compositionLayout.setMargin(false);


            compositionLayout.addComponent(frontAttachment);
            compositionLayout.addComponent(backAttachment);
            compositionLayout.addComponents(documentType, expiryDateOptions, row1, row2, row3, row4, row5, row6);
            compositionLayout.addComponent(placeOfBirth);

            if (context.requiresFeature(StandardFeatures.FEATURE_ACTIONABLE)) {
                Button actionButton = new Button("Save");
                actionButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
                actionButton.addClickListener((event) -> submit());

                compositionLayout.addComponent(actionButton);
                compositionLayout.setComponentAlignment(actionButton, Alignment.BOTTOM_RIGHT);
            }

        } else if (context.requiresFeature(StandardFeatures.FEATURE_HORIZONTAL_VIEW)) {
            compositionLayout = new HorizontalLayout();
            compositionLayout.setMargin(false);

            VerticalLayout col1 = new VerticalLayout();
            col1.setMargin(false);
            col1.addComponents(frontAttachment, backAttachment, documentType, expiryDateOptions);

            if (context.requiresFeature(StandardFeatures.FEATURE_ACTIONABLE)) {
                Button actionButton = new Button("Save");
                actionButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
                actionButton.addClickListener((event) -> submit());

                col1.addComponent(actionButton);
            }

            VerticalLayout col2 = new VerticalLayout();
            col2.setMargin(false);
            col2.addComponents(row1, row2, row3);

            VerticalLayout col3 = new VerticalLayout();
            col3.setMargin(false);
            col3.addComponents(row4, row5, row6);

            compositionLayout.addComponents(col1, col2, col3);
            compositionLayout.setExpandRatio(col1, 1);
            compositionLayout.setExpandRatio(col2, 1);
            compositionLayout.setExpandRatio(col3, 1);

            compositionLayout.setWidth(100, Unit.PERCENTAGE);
        } else {
            throw new IllegalArgumentException("Layout feature is not defined");
        }

        setCompositionRoot(compositionLayout);
    }

    void submit() {
        IdentificationDocumentModel model = binder.getBean();
        if (!isValid()) return;

        if (binder.validate().isOk()) {
            SaveIdentificationDocumentRequest request = new SaveIdentificationDocumentRequest();

            SaveIdentificationDocumentRequest.Attachment backAttachment = new SaveIdentificationDocumentRequest.Attachment();
            backAttachment.setFileId(model.getBackAttachment().getFileId());
            backAttachment.setFileName(model.getBackAttachment().getName());
            SaveIdentificationDocumentRequest.Attachment frontAttachment = new SaveIdentificationDocumentRequest.Attachment();
            frontAttachment.setFileId(model.getFrontAttachment().getFileId());
            frontAttachment.setFileName(model.getFrontAttachment().getName());
            request.setBackAttachment(backAttachment);
            request.setFrontAttachment(frontAttachment);

            request.setDocumentNumber(model.getDocumentNumber());
            request.setDocumentType(model.getDocumentType());

            request.setName(model.getName());
            request.setSurname1(model.getSurname1());
            request.setSurname2(model.getSurname2());
            request.setGender(model.getGender());
            request.setNationality(model.getNationality());

            request.setDateOfBirth(model.getDateOfBirth());
            request.setCity(model.getCity());
            request.setStreet(model.getStreet());
            request.setHouse(model.getHouse());
            request.setProvince(model.getProvince());
            request.setPlaceOfBirth(model.getPlaceOfBirth());
            request.setExpirationDate(model.getExpirationDate());
            request.setClientId(model.getClientId());
            request.setTaskId(model.getTaskId());

            Call<Void> call = ApiAccessor.gI().get(AlfaApiClient.class).saveIdentificationDocument(request);
            BackgroundOperations.callApi("Saving Identification document", call, t -> {
                Notifications.trayNotification("Identification document saved");
                if (successCallback != null) {
                    successCallback.accept(null);
                }
            }, Notifications::errorNotification);
        }
    }
    private boolean isValid() {
        expirationDate.setComponentError(null);
        expiryDateOptions.setComponentError(null);

        boolean isValid = true;
        if (expiryDateOptions.isVisible() && expiryDateOptions.getValue() == null) {
            expiryDateOptions.setComponentError(new UserError("Value is required"));
            isValid = false;
        }
        if (SaveIdentificationDocumentRequest.DocumentType.DNI.equals(binder.getBean().getDocumentType()) || SaveIdentificationDocumentRequest.DocumentType.PASSPORT.equals(binder.getBean().getDocumentType())) {
            if (binder.getBean().getExpirationDate() == null) {
                expirationDate.setComponentError(new UserError("Expiration date is required"));
                isValid = false;
            }
        }
        if (SaveIdentificationDocumentRequest.DocumentType.NIE.equals(binder.getBean().getDocumentType()) && HAS_EXPIRATION_DATE.equals(expiryDateOptions.getValue())) {
            if (binder.getBean().getExpirationDate() == null) {
                expirationDate.setComponentError(new UserError("Expiration date is required"));
                isValid = false;
            }
        }
        if (SaveIdentificationDocumentRequest.DocumentType.PASSPORT.equals(binder.getBean().getDocumentType())) {
            if (binder.getBean().getGender() == null) {
                gender.setComponentError(new UserError("Gender is required"));
            }
        }
        return isValid;
    }

    public void setSuccessCallback(Consumer<Void> successCallback) {
        this.successCallback = successCallback;
    }
}
