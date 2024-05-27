package fintech.bo.spain.alfa.task;

import com.google.common.base.MoreObjects;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import fintech.Validate;
import fintech.bo.components.AbstractBackofficeUI;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.Formats;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.application.LoanApplicationComponents;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.client.repository.ClientRepository;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.task.CommonTaskView;
import fintech.bo.components.task.ReassignTaskDialog;
import fintech.bo.components.task.TaskConstants;
import fintech.bo.components.utils.BigDecimalUtils;
import fintech.bo.db.jooq.instantor.tables.records.ResponseRecord;
import fintech.bo.db.jooq.instantor.tables.records.TransactionRecord;
import fintech.bo.db.jooq.lending.tables.records.LoanApplicationRecord;
import fintech.bo.db.jooq.task.tables.records.TaskAttributeRecord;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.bo.spain.alfa.db.jooq.alfa.tables.records.WealthinessCategoryRecord;
import fintech.bo.spain.alfa.db.jooq.alfa.tables.records.WealthinessRecord;
import fintech.spain.alfa.bo.model.SaveTransactionCategoryRequest;
import org.jooq.DSLContext;
import org.jooq.Result;
import retrofit2.Call;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static fintech.bo.components.Formats.decimalRenderer;
import static fintech.bo.components.GridHelper.alignRightStyle;
import static fintech.bo.db.jooq.instantor.Tables.RESPONSE;
import static fintech.bo.db.jooq.instantor.Tables.TRANSACTION;
import static fintech.bo.spain.alfa.db.jooq.alfa.Tables.WEALTHINESS;
import static fintech.bo.spain.alfa.db.jooq.alfa.Tables.WEALTHINESS_CATEGORY;

public class WealthinessCheckTask  extends CommonTaskView {

    private final ClientRepository clientRepository;
    private ResponseRecord instantorRecord;
    private final AlfaApiClient apiClient;
    private WealthinessRecord wealthinessRecord;
    private Long wealthinessId;
    private Set<String> categoryNames;
    private final Grid<WealthinessCategoryRecord> categoriesGrid = new Grid<>();
    private FooterCell nordigenTotal;
    private FooterCell manualTotal;
    private ClientDTO client;
    private LoanApplicationRecord application;

    public WealthinessCheckTask() {
        this.apiClient = ApiAccessor.gI().get(AlfaApiClient.class);
        this.clientRepository = ApiAccessor.gI().get(ClientRepository.class);
    }

    @Override
    public Component buildView(BusinessObjectLayout baseLayout) {
        baseLayout.setSplitPosition(550);

        Optional<TaskAttributeRecord> wealthinessAttribute = getHelper().getTaskQueries().findAttributeByKey(getTask().getId(), "WealthinessId");
        Validate.isTrue(wealthinessAttribute.isPresent(), "No wealthiness attribute available in workflow");
        wealthinessId = Long.valueOf(wealthinessAttribute.get().getValue());

        this.client = clientRepository.getRequired(getTask().getClientId());
        this.application = getHelper().getLoanApplicationQueries().findById(getTask().getApplicationId());

        categoriesGrid();

        reloadWealthiness();

        instantorRecord = getHelper().getDb().selectFrom(RESPONSE).where(RESPONSE.ID.eq(wealthinessRecord.getInstantorResponseId())).fetchOne();
        Validate.notNull(instantorRecord, "Instantor response not found by id [%s]", wealthinessRecord.getInstantorResponseId());

        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(clientComponent());
        layout.addComponent(summary());
        if (BigDecimalUtils.isNegative(application.getScore())) {
            layout.addComponent(reassignComponent());
        }
        layout.addComponent(getHelper().completeTaskComponent(getTask(), resolutions()));
        baseLayout.addTab("Transactions", this::transactionsTab);
        return layout;
    }

    private Component reassignComponent() {
        Button reassing = new Button("Reassign");
        reassing.addStyleName(ValoTheme.BUTTON_PRIMARY);
        reassing.addClickListener(e -> reassign());
        return reassing;
    }

    private void reassign() {
        ReassignTaskDialog dialog = getHelper().getTaskComponents().reassignTaskDialog(getTask().getId());
        dialog.addCloseListener(e -> {
            if (dialog.isReassigned()) {
                AbstractBackofficeUI ui = (AbstractBackofficeUI) UI.getCurrent();
                ui.getTabSheetNavigator().closeCurrentTab();
                getHelper().takeNextTask();
            }
        });
        UI.getCurrent().addWindow(dialog);
    }

    private Component summary() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(new MarginInfo(true, false, true, false));
        layout.setSpacing(false);
        layout.setWidth(100, Sizeable.Unit.PERCENTAGE);
        layout.setHeight(300, Sizeable.Unit.PIXELS);

        Label title = new Label("Wealthiness");
        title.addStyleName(ValoTheme.LABEL_H4);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        title.addStyleName(ValoTheme.LABEL_COLORED);
        layout.addComponent(title);

        categoriesGrid.setSizeFull();
        layout.addComponentsAndExpand(categoriesGrid);
        return layout;
    }

    private void categoriesGrid() {
        categoriesGrid.addColumn(WealthinessCategoryRecord::getCategory).setCaption("Category");
        categoriesGrid.addColumn(WealthinessCategoryRecord::getWeightInPrecent).setCaption("Weight %").setRenderer(decimalRenderer()).setStyleGenerator(alignRightStyle()).setWidth(100);
        categoriesGrid.addColumn(WealthinessCategoryRecord::getNordigenWeightedWealthiness).setId("nordigen").setCaption("Nordigen").setRenderer(decimalRenderer()).setStyleGenerator(alignRightStyle()).setWidth(100);
        categoriesGrid.addColumn(WealthinessCategoryRecord::getManualWeightedWealthiness).setId("manual").setCaption("Manual").setRenderer(decimalRenderer()).setStyleGenerator(alignRightStyle()).setWidth(100);

        FooterRow footerRow = categoriesGrid.appendFooterRow();
        nordigenTotal = footerRow.getCell("nordigen");
        nordigenTotal.setStyleName("v-align-right");
        manualTotal = footerRow.getCell("manual");
        manualTotal.setStyleName("v-align-right");
    }

    private void reloadWealthiness() {
        wealthinessRecord = getHelper().getDb().selectFrom(WEALTHINESS).where(WEALTHINESS.ID.eq(wealthinessId)).fetchOne();
        Validate.notNull(wealthinessRecord, "Wealthiness record not found by id [%s]", wealthinessId);
        List<WealthinessCategoryRecord> categories = getHelper().getDb().selectFrom(WEALTHINESS_CATEGORY).where(WEALTHINESS_CATEGORY.WEALTHINESS_ID.eq(wealthinessId)).orderBy(WEALTHINESS_CATEGORY.CATEGORY.asc()).fetch();
        categoryNames = categories.stream().map(WealthinessCategoryRecord::getCategory).collect(Collectors.toCollection(LinkedHashSet::new));
        categoriesGrid.setItems(categories);
        nordigenTotal.setText(Formats.decimalFormat().format(wealthinessRecord.getNordigenWeightedWealthiness()));
        manualTotal.setText(Formats.decimalFormat().format(wealthinessRecord.getManualWeightedWealthiness()));
    }

    private Component transactionsTab() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(false);

        DSLContext db = getHelper().getDb();
        Result<TransactionRecord> transactions = db.selectFrom(TRANSACTION)
            .where(TRANSACTION.RESPONSE_ID.eq(instantorRecord.getId()))
            .and(TRANSACTION.DATE.greaterOrEqual(wealthinessRecord.getPeriodFrom()))
            .and(TRANSACTION.DATE.lessOrEqual(wealthinessRecord.getPeriodTo()))
            .and(TRANSACTION.ACCOUNT_NUMBER.equalIgnoreCase(wealthinessRecord.getAccountNumber()))
            .orderBy(TRANSACTION.ACCOUNT_NUMBER.asc(), TRANSACTION.DATE.desc()).fetch();

        Grid<TransactionRecord> grid = new Grid<>();
        grid.addColumn(r -> {
            if (canEdit()) {
                ComboBox<String> category = new ComboBox<>();
                category.setTextInputAllowed(false);
                category.setPlaceholder("Select category...");
                category.setItems(categoryNames);
                category.setValue(MoreObjects.firstNonNull(r.getCategory(), ""));
                category.addStyleName(ValoTheme.COMBOBOX_BORDERLESS);
                category.setSizeFull();
                category.addValueChangeListener(event -> {

                    SaveTransactionCategoryRequest request = new SaveTransactionCategoryRequest();
                    request.setWealthinessId(this.wealthinessId);
                    request.setTransactionId(r.getId());
                    request.setCategory(event.getValue());
                    Call<Void> call = apiClient.saveTransactionCategory(request);
                    BackgroundOperations.callApi("Saving", call, t -> {
                        Notifications.trayNotification("Category saved");
                        reloadWealthiness();
                    }, Notifications::errorNotification);

                    r.setCategory(event.getValue());
                });
                return category;
            } else {
                return new Label(r.getCategory());
            }
        }, new ComponentRenderer()).setWidth(200).setSortable(false).setCaption("Category");
        grid.addColumn(TransactionRecord::getDate).setCaption("Date");
        grid.addColumn(TransactionRecord::getAmount).setCaption("Amount").setRenderer(decimalRenderer()).setStyleGenerator(amountStyle);
        grid.addColumn(TransactionRecord::getBalance).setCaption("Balance");
        grid.addColumn(TransactionRecord::getDescription).setCaption("Description");
        grid.addColumn(TransactionRecord::getCurrency).setCaption("Currency");
        grid.addColumn(TransactionRecord::getNordigenCategory).setCaption("Nordigen Category");
        grid.setItems(transactions);
        grid.setRowHeight(35);
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        layout.addComponentsAndExpand(grid);

        return layout;
    }

    private boolean canEdit() {
        return LoginService.getLoginData().getUser().equals(getTask().getAgent()) && TaskConstants.STATUS_OPEN.equals(getTask().getStatus());
    }

    private static final StyleGenerator<TransactionRecord> amountStyle = (StyleGenerator<TransactionRecord>) item -> {
        if (item.getAmount().doubleValue() < 0) {
            return "v-align-right text-bold " + BackofficeTheme.TEXT_DANGER;
        } else {
            return "v-align-right text-bold " + BackofficeTheme.TEXT_SUCCESS;
        }
    };

    private Component clientComponent() {
        PropertyLayout layout = new PropertyLayout("Client");
        layout.add("First name", client.getFirstName());
        layout.add("Last name", client.getLastName());
        layout.add("Second last name", client.getSecondLastName());
        layout.add("Account number", client.getAccountNumber());
        layout.add("Score", LoanApplicationComponents.scoreBar(application));
        layout.add("Score bucket", application.getScoreBucket());
        layout.add("Score value", application.getScore());
        layout.setMargin(false);
        return layout;
    }

}
