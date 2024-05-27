package fintech.bo.components.cms;

import com.google.common.base.MoreObjects;
import com.vaadin.data.Binder;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import fintech.bo.api.client.CmsApiClient;
import fintech.bo.api.model.cms.CmsDocumentationResponse;
import fintech.bo.api.model.cms.RenderCmsItemRequest;
import fintech.bo.api.model.cms.RenderNotificationResponse;
import fintech.bo.api.model.cms.UpdateCmsItemRequest;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.cms.tables.Locale;
import fintech.bo.db.jooq.cms.tables.records.LocaleRecord;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import retrofit2.Call;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static fintech.bo.components.cms.CmsComponents.emailPreviewPanel;
import static fintech.bo.components.cms.CmsComponents.smsPreviewPanel;
import static fintech.bo.db.jooq.cms.tables.Item.ITEM;

public class EditCmsItemDialog extends ActionDialog {

    private final CmsApiClient cmsApiClient;

    private final Collection<String> locales;
    private final String defaultLocale;
    private final Binder<UpdateCmsItemRequest.CmsItem> binder = new Binder<>();
    private final UpdateCmsItemRequest updateRequest;
    private final String itemType;


    public EditCmsItemDialog(CmsApiClient cmsApiClient, DSLContext db, String itemKey) {
        super(itemKey, "Save");
        this.cmsApiClient = cmsApiClient;

        List<LocaleRecord> localeRecords = db.selectFrom(Locale.LOCALE).fetchInto(LocaleRecord.class);
        this.defaultLocale = localeRecords.stream()
            .filter(LocaleRecord::getIsDefault)
            .findFirst()
            .map(LocaleRecord::getLocale)
            .orElse(null);

        this.locales = localeRecords.stream()
            .map(LocaleRecord::getLocale)
            .collect(Collectors.toList());

        this.updateRequest = new UpdateCmsItemRequest();
        db.selectFrom(ITEM)
            .where(ITEM.ITEM_KEY.eq(itemKey))
            .forEach(itemRecord -> updateRequest.getItems().put(itemRecord.getLocale(), new UpdateCmsItemRequest.CmsItem()
                .setKey(itemRecord.getItemKey())
                .setLocale(itemRecord.getLocale())
                .setDescription(itemRecord.getDescription())
                .setScope(itemRecord.getScope())
                .setItemType(itemRecord.getItemType())
                .setEmailSubjectTemplate(itemRecord.getEmailSubjectTemplate())
                .setEmailBodyTemplate(itemRecord.getEmailBodyTemplate())
                .setSmsTextTemplate(itemRecord.getSmsTextTemplate())
                .setContentTemplate(itemRecord.getContentTemplate())
                .setTitleTemplate(itemRecord.getTitleTemplate())
                .setHeaderTemplate(itemRecord.getHeaderTemplate())
                .setFooterTemplate(itemRecord.getFooterTemplate())
            ));

        this.itemType = this.updateRequest.getItems().get(defaultLocale).getItemType();

        updateForm(defaultLocale);

        TabSheet tabSheet = new TabSheet();
        tabSheet.addTab(editor(), "Edit");
        if (!CmsConstants.TYPE_TRANSLATION.equals(this.itemType)) {
            tabSheet.addTab(help(), "Help");
        }
        tabSheet.setSizeFull();

        setDialogContent(tabSheet);
        setWidth(800, Unit.PIXELS);
        fullHeight();
    }

    private void updateForm(String locale) {
        binder.setBean(updateRequest.getItems().computeIfAbsent(locale, this::copyDefaultTemplate));
    }

    private UpdateCmsItemRequest.CmsItem copyDefaultTemplate(String locale) {
        UpdateCmsItemRequest.CmsItem defaultTemplate = updateRequest.getItems().get(defaultLocale);
        UpdateCmsItemRequest.CmsItem copy = new UpdateCmsItemRequest.CmsItem()
            .setKey(defaultTemplate.getKey())
            .setLocale(locale)
            .setDescription(defaultTemplate.getDescription())
            .setScope(defaultTemplate.getScope())
            .setItemType(defaultTemplate.getItemType());

        Dialogs.confirm("Template in this locale does not exist. Copy content from template in default locale?", event -> {
            copy.setEmailSubjectTemplate(defaultTemplate.getEmailSubjectTemplate());
            copy.setEmailBodyTemplate(defaultTemplate.getEmailBodyTemplate());
            copy.setSmsTextTemplate(defaultTemplate.getSmsTextTemplate());
            copy.setContentTemplate(defaultTemplate.getContentTemplate());
            copy.setTitleTemplate(defaultTemplate.getTitleTemplate());
            copy.setHeaderTemplate(defaultTemplate.getHeaderTemplate());
            copy.setFooterTemplate(defaultTemplate.getFooterTemplate());
            binder.readBean(copy);
        });

        return copy;
    }

    private Component help() {
        TextArea docs = new TextArea("Template engine variables");
        docs.setReadOnly(true);
        docs.addStyleName(BackofficeTheme.TEXT_MONO);
        docs.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(docs);
        layout.addComponent(new Label("<a href=\"https://pebbletemplates.io\" target=\"_blank\">Pebble template engine</a>", ContentMode.HTML));
        layout.setExpandRatio(docs, 1.0f);
        layout.setSizeFull();

        Call<CmsDocumentationResponse> call = cmsApiClient.getDocumentation();
        BackgroundOperations.callApiSilent(call,
            response -> docs.setValue(MoreObjects.firstNonNull(response.getTestingContextDocumentation(), "")),
            Notifications::errorNotification);
        return layout;
    }

    private VerticalLayout editor() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        HorizontalLayout topBar = new HorizontalLayout();
        ComboBox<String> localeSelector = new ComboBox<>(null, locales);
        localeSelector.setEmptySelectionAllowed(false);
        localeSelector.setTextInputAllowed(false);
        localeSelector.setSelectedItem(defaultLocale);
        localeSelector.addValueChangeListener(event -> updateForm(event.getValue()));

        Button previewButton = new Button("Preview");
        previewButton.setVisible(false);

        Label locale = new Label("Locale");
        topBar.addComponents(locale, localeSelector);
        topBar.addComponentsAndExpand(new VerticalLayout());
        topBar.addComponent(previewButton);
        topBar.setComponentAlignment(locale, Alignment.MIDDLE_LEFT);
        topBar.setWidth(100, Unit.PERCENTAGE);

        layout.addComponent(topBar);

        if (CmsConstants.TYPE_NOTIFICATION.equals(this.itemType)) {

            previewButton.setVisible(true);
            previewButton.addClickListener(e -> previewNotification(binder.getBean()));

            TextArea smsText = new TextArea("SMS text");
            smsText.setWordWrap(false);
            smsText.setRows(6);
            smsText.setWidth(100, Unit.PERCENTAGE);
            smsText.addStyleName(BackofficeTheme.TEXT_MONO);
            layout.addComponent(smsText);
            binder.bind(smsText, UpdateCmsItemRequest.CmsItem::getSmsTextTemplate, UpdateCmsItemRequest.CmsItem::setSmsTextTemplate);

            TextField emailSubject = new TextField("Email subject");
            emailSubject.setWidth(100, Unit.PERCENTAGE);
            binder.bind(emailSubject, UpdateCmsItemRequest.CmsItem::getEmailSubjectTemplate, UpdateCmsItemRequest.CmsItem::setEmailSubjectTemplate);

            TextArea emailBody = new TextArea("Email body");
            emailBody.setWordWrap(false);
            emailBody.setSizeFull();
            emailBody.setWidth(100, Unit.PERCENTAGE);
            emailBody.addStyleName(BackofficeTheme.TEXT_MONO);

            layout.addComponent(emailSubject);
            layout.addComponent(emailBody);
            layout.setExpandRatio(emailBody, 10);
            binder.bind(emailBody, UpdateCmsItemRequest.CmsItem::getEmailBodyTemplate, UpdateCmsItemRequest.CmsItem::setEmailBodyTemplate);
        }

        if (CmsConstants.TYPE_EMBEDDABLE.equals(this.itemType) || CmsConstants.TYPE_TRANSLATION.equals(this.itemType)) {
            AceEditor jsonEditor = new AceEditor();
            jsonEditor.setMode(AceMode.json);
            jsonEditor.setWordWrap(false);
            jsonEditor.setSizeFull();
            jsonEditor.setCaption("Content");
            layout.addComponentsAndExpand(jsonEditor);
            binder.bind(jsonEditor, UpdateCmsItemRequest.CmsItem::getContentTemplate, UpdateCmsItemRequest.CmsItem::setContentTemplate);
        }

        if (CmsConstants.TYPE_PDF_HTML.equals(this.itemType)) {
            previewButton.setVisible(true);
            previewButton.addClickListener(e -> previewPdf(binder.getBean()));

            TextField header = new TextField("Header");
            header.setWidth(100, Unit.PERCENTAGE);
            binder.bind(header, UpdateCmsItemRequest.CmsItem::getHeaderTemplate, UpdateCmsItemRequest.CmsItem::setHeaderTemplate);

            TextField title = new TextField("Title");
            title.setWidth(100, Unit.PERCENTAGE);
            binder.bind(title, UpdateCmsItemRequest.CmsItem::getTitleTemplate, UpdateCmsItemRequest.CmsItem::setTitleTemplate);

            TextArea content = new TextArea("Content");
            content.setWordWrap(false);
            content.setSizeFull();
            content.setWidth(100, Unit.PERCENTAGE);
            content.addStyleName(BackofficeTheme.TEXT_MONO);

            TextField footer = new TextField("Footer");
            footer.setWidth(100, Unit.PERCENTAGE);
            binder.bind(footer, UpdateCmsItemRequest.CmsItem::getFooterTemplate, UpdateCmsItemRequest.CmsItem::setFooterTemplate);

            layout.addComponents(header, footer, title);
            layout.addComponentsAndExpand(content);
            binder.bind(content, UpdateCmsItemRequest.CmsItem::getContentTemplate, UpdateCmsItemRequest.CmsItem::setContentTemplate);
        }
        return layout;
    }

    @Override
    protected void executeAction() {
        Call<Void> call = cmsApiClient.saveItem(updateRequest);
        BackgroundOperations.callApi("Saving CMS item", call, t -> {
            close();
            Notifications.trayNotification("Saved successfully");
        }, Notifications::errorNotification);
    }

    private void previewPdf(UpdateCmsItemRequest.CmsItem item) {
        Call<ResponseBody> call = cmsApiClient.renderPdf(mapRequest(item));
        BackgroundOperations.callApi("Rendering PDF", call, renderResponse -> {
            try {
                String filename = item.getKey() + ".pdf";
                StreamResource resource = new StreamResource(renderResponse::byteStream, filename);
                resource.setMIMEType("application/pdf");
                resource.getStream().setParameter("Content-Disposition", "attachment; filename=" + filename);

                BrowserFrame frame = new BrowserFrame(filename, resource);
                frame.setSizeFull();

                Window window = new Window("Report");
                window.center();
                window.setModal(true);
                window.setHeight(90, Unit.PERCENTAGE);
                window.setWidth(90, Unit.PERCENTAGE);
                window.setContent(frame);
                getUI().addWindow(window);
            } finally {
                renderResponse.close();
            }
        }, Notifications::errorNotification);
    }

    private void previewNotification(UpdateCmsItemRequest.CmsItem item) {
        Call<RenderNotificationResponse> call = cmsApiClient.previewNotification(mapRequest(item));
        BackgroundOperations.callApi("Rendering notification", call, renderResponse -> {
            VerticalLayout layout = new VerticalLayout();
            if (!StringUtils.isBlank(renderResponse.getSmsText())) {
                layout.addComponent(smsPreviewPanel(renderResponse.getSmsText()));
            }
            if (!StringUtils.isBlank(renderResponse.getEmailSubject()) || !StringUtils.isBlank(renderResponse.getEmailBody())) {
                layout.addComponent(emailPreviewPanel(renderResponse.getEmailSubject(), renderResponse.getEmailBody()));
            }
            Window previewWindow = new Window(item.getKey());
            previewWindow.setContent(layout);
            previewWindow.center();
            previewWindow.setModal(true);
            getUI().addWindow(previewWindow);
        }, Notifications::errorNotification);
    }

    private RenderCmsItemRequest mapRequest(UpdateCmsItemRequest.CmsItem item) {
        RenderCmsItemRequest renderCmsItemRequest = new RenderCmsItemRequest();
        renderCmsItemRequest.setKey(item.getKey());
        renderCmsItemRequest.setLocale(item.getLocale());
        renderCmsItemRequest.setDescription(item.getDescription());
        renderCmsItemRequest.setScope(item.getScope());
        renderCmsItemRequest.setItemType(item.getItemType());
        renderCmsItemRequest.setEmailSubjectTemplate(item.getEmailSubjectTemplate());
        renderCmsItemRequest.setEmailBodyTemplate(item.getEmailBodyTemplate());
        renderCmsItemRequest.setSmsTextTemplate(item.getSmsTextTemplate());
        renderCmsItemRequest.setContentTemplate(item.getContentTemplate());
        renderCmsItemRequest.setTitleTemplate(item.getTitleTemplate());
        renderCmsItemRequest.setHeaderTemplate(item.getHeaderTemplate());
        renderCmsItemRequest.setFooterTemplate(item.getFooterTemplate());
        return renderCmsItemRequest;
    }

}
