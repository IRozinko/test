package fintech.bo.components.cms;

import com.vaadin.data.provider.Query;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.CmsApiClient;
import fintech.bo.api.model.cms.AddNewLocaleRequest;
import fintech.bo.api.model.cms.DeleteLocaleRequest;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.notifications.Notifications;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.cms.tables.Locale.LOCALE;

public class ManageLocalesDialog extends Window {

    private final CmsApiClient apiClient;
    private final DSLContext db;
    private JooqDataProvider<Record> dataProvider;

    public ManageLocalesDialog() {
        super("Manage locales");
        setWidth(400, Unit.PIXELS);

        this.apiClient = ApiAccessor.gI().get(CmsApiClient.class);
        this.db = ApiAccessor.gI().get(DSLContext.class);

        HorizontalLayout dialogActions = new HorizontalLayout();
        Button closeButton = new Button("Close");
        closeButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        closeButton.addClickListener(e -> close());
        dialogActions.addComponent(closeButton);
        dialogActions.setWidth(100, Unit.PERCENTAGE);
        dialogActions.setComponentAlignment(closeButton, Alignment.MIDDLE_RIGHT);

        setContent(new VerticalLayout(createContent(), dialogActions));

        center();
        setModal(true);
    }

    private Component createContent() {
        VerticalLayout layout = new VerticalLayout();
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.setShowTotalCount(false);
        builder.addColumn(LOCALE.LOCALE_).setWidth(100).setCaption("Locale");
        builder.addColumn(LOCALE.IS_DEFAULT).setWidth(100).setCaption("Default");
        builder.addActionColumn("Delete", this::deleteLocale, r -> r.get(LOCALE.IS_DEFAULT).equals(Boolean.TRUE));
        dataProvider = new JooqDataProvider<Record>(db) {
            @Override
            protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
                SelectWhereStep<Record> select = db.select(fields(new Field[]{LOCALE.LOCALE_}, LOCALE.IS_DEFAULT))
                    .from(LOCALE);
                return select;
            }

            @Override
            protected Object id(Record item) {
                return item.get(LOCALE.LOCALE_);
            }
        };
        Grid<Record> localeGrid = builder.build(dataProvider);
        localeGrid.setHeightByRows(3);
        layout.addComponent(localeGrid);

        TextField locale = new TextField("Add new locale");
        Button addLocaleButton = new Button("Add");
        addLocaleButton.addClickListener(event -> {
            AddNewLocaleRequest request = new AddNewLocaleRequest();
            request.setLocale(locale.getValue());
            BackgroundOperations.callApi("Adding locale", apiClient.addNewLocale(request),
                t -> dataProvider.refreshAll(),
                Notifications::errorNotification);
        });

        HorizontalLayout addForm = new HorizontalLayout(locale, addLocaleButton);
        addForm.setComponentAlignment(addLocaleButton, Alignment.BOTTOM_LEFT);
        layout.addComponent(addForm);
        return layout;
    }

    private void deleteLocale(Record record) {
        Dialogs.confirm("Deleting locale will also delete all CMS items for this locale. Do you want to proceed?", event -> {
            DeleteLocaleRequest request = new DeleteLocaleRequest();
            request.setLocale(record.get(LOCALE.LOCALE_));

            BackgroundOperations.callApi("Deleting locale", apiClient.deleteLocale(request),
                t -> dataProvider.refreshAll(),
                Notifications::errorNotification);
        });
    }

}
