package fintech.bo.components.application;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.StyleGenerator;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.ProductResolver;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.workflow.WorkflowConstants;
import fintech.bo.components.workflow.WorkflowQueries;
import fintech.bo.db.jooq.lending.tables.records.LoanApplicationRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.math.BigDecimal;

import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.components.utils.BigDecimalUtils.amount;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.lending.Tables.LOAN_APPLICATION;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;

public abstract class LoanApplicationComponents {

    private final DSLContext db;
    private final WorkflowQueries workflowQueries;
    private final LoanApplicationQueries applicationQueries;
    private final JooqClientDataService jooqClientDataService;

    protected LoanApplicationComponents(DSLContext db,
                                        WorkflowQueries workflowQueries,
                                        LoanApplicationQueries applicationQueries,
                                        JooqClientDataService jooqClientDataService) {
        this.db = db;
        this.workflowQueries = workflowQueries;
        this.applicationQueries = applicationQueries;
        this.jooqClientDataService = jooqClientDataService;
    }

    public static String applicationLink(Long id) {
        return AbstractLoanApplicationView.NAME + "/" + id;
    }

    protected static StyleGenerator<Record> statusDetailStyle() {
        return item -> {
            String status = item.get(LOAN_APPLICATION.STATUS_DETAIL);
            if (LoanApplicationConstants.STATUS_DETAIL_APPROVED.equals(status)) {
                return BackofficeTheme.TEXT_SUCCESS;
            } else if (LoanApplicationConstants.STATUS_DETAIL_CANCELLED.equals(status)) {
                return BackofficeTheme.TEXT_GRAY;
            } else if (LoanApplicationConstants.STATUS_DETAIL_PENDING.equals(status)) {
                return BackofficeTheme.TEXT_ACTIVE;
            } else if (LoanApplicationConstants.STATUS_DETAIL_REJECTED.equals(status)) {
                return BackofficeTheme.TEXT_DANGER;
            } else {
                return "";
            }
        };
    }

    public static com.vaadin.ui.Component scoreBar(LoanApplicationRecord record) {
        return scoreBar(record.getScoreBucket(), record.getScore());
    }

    public static com.vaadin.ui.Component scoreBar(String bucket, BigDecimal score) {
        if (StringUtils.isBlank(bucket) || score == null) {
            return new Label();
        }
        ProgressBar bar = new ProgressBar();
        bar.setDescription(String.format("%s, %s", bucket, score));
        bar.setWidth(80, Sizeable.Unit.PIXELS);
        if ("RED".equals(bucket)) {
            bar.setValue(100.0f);
            bar.addStyleName("red");
        } else if ("GREEN".equals(bucket)) {
            bar.setValue(100.0f);
            bar.addStyleName("green");
        } else {
            float barValue = MoreObjects.firstNonNull(score, amount(0)).divide(amount(100), 2, BigDecimal.ROUND_HALF_UP).floatValue();
            if (barValue < 0) {
                bar.setValue(100.00f);
                bar.addStyleName("red");
            } else {
                bar.setValue(barValue);
                bar.addStyleName("yellow");
            }
        }
        return bar;
    }

    public ComboBox<Record> clientLoanApplicationsComboBox() {
        ComboBox<Record> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Select loan application...");
        comboBox.setPageLength(20);
        comboBox.setPopupWidth("600px");
        comboBox.setItemCaptionGenerator(r -> r.get(LOAN_APPLICATION.APPLICATION_NUMBER));
        return comboBox;
    }

    public ComboBox<String> typeComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Application type");
        comboBox.setItems(applicationQueries.findTypes());
        comboBox.setTextInputAllowed(false);
        comboBox.setCaption("Type");
        return comboBox;
    }

    public ComboBox<String> statusComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Application status");
        comboBox.setTextInputAllowed(false);
        comboBox.setPopupWidth(null);
        comboBox.setCaption("Status");
        comboBox.setItems(
            LoanApplicationConstants.STATUS_DETAIL_PENDING,
            LoanApplicationConstants.STATUS_DETAIL_APPROVED,
            LoanApplicationConstants.STATUS_DETAIL_REJECTED,
            LoanApplicationConstants.STATUS_DETAIL_CANCELLED
        );
        return comboBox;
    }

    public ComboBox<String> workflowStepComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Workflow step");
        comboBox.setItems(workflowQueries.findActivityNames(WorkflowConstants.ACTIVITY_STATUS_ACTIVE));
        comboBox.setTextInputAllowed(false);
        comboBox.setPageLength(20);
        comboBox.setWidth(250, Sizeable.Unit.PIXELS);
        comboBox.setCaption("Workflow step");
        return comboBox;
    }

    public ComboBox<String> sourceType() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Source Type");
        comboBox.setItems(LoanApplicationConstants.SOURCE_TYPE_ORGANIC, LoanApplicationConstants.SOURCE_TYPE_AFFILIATE);
        comboBox.setTextInputAllowed(false);
        comboBox.setPageLength(20);
        comboBox.setCaption("Source Type");
        comboBox.setWidth(100, Sizeable.Unit.PIXELS);
        return comboBox;
    }

    public ComboBox<String> sourceName() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Source Name");
        comboBox.setCaption("Source Name");
        comboBox.setItems(applicationQueries.findSourceNames());
        comboBox.setTextInputAllowed(false);
        comboBox.setPageLength(250);
        comboBox.setWidth(100, Sizeable.Unit.PIXELS);
        return comboBox;
    }

    public ComboBox<String> closeReason() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Close reason");
        comboBox.setCaption("Close reason");
        comboBox.setItems(applicationQueries.findCloseReasons());
        comboBox.setTextInputAllowed(false);
        comboBox.setPageLength(250);
        comboBox.setWidth(150, Sizeable.Unit.PIXELS);
        return comboBox;
    }

    public LoanApplicationDataProvider dataProvider() {
        return new LoanApplicationDataProvider(db, jooqClientDataService);
    }

    public Grid<Record> grid(LoanApplicationDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        addGridColumns(builder);
        builder.sortDesc(LOAN_APPLICATION.CREATED_AT);
        return builder.build(dataProvider);
    }

    protected void addGridColumns(JooqGridBuilder<Record> builder) {
        builder.addNavigationColumn("Open", r -> "loan-application/" + r.get(LOAN_APPLICATION.ID));
        builder.addColumn(LOAN_APPLICATION.APPLICATION_NUMBER);
        builder.addColumn(LOAN_APPLICATION.TYPE);
        builder.addComponentColumn((ValueProvider<Record, com.vaadin.ui.Component>) record -> LoanApplicationComponents.scoreBar(record.get(LOAN_APPLICATION.SCORE_BUCKET), record.get(LOAN_APPLICATION.SCORE))).setSortable(false).setWidth(120).setCaption("Score");
        builder.addColumn(LOAN_APPLICATION.STATUS);
        builder.addColumn(LOAN_APPLICATION.STATUS_DETAIL).setStyleGenerator(statusDetailStyle());
        builder.addColumn(LOAN_APPLICATION.CLOSE_DATE);
        builder.addColumn(LOAN_APPLICATION.CLOSE_REASON).setWidth(200);
        builder.addLinkColumn(FIELD_CLIENT_NAME, r -> ClientComponents.clientLink(r.get(LOAN_APPLICATION.CLIENT_ID)));
        builder.addColumn(CLIENT.DOCUMENT_NUMBER);
        builder.addColumn(CLIENT.PHONE);
        builder.addLinkColumn(LOAN.LOAN_NUMBER, r -> LoanComponents.loanLink(r.get(LOAN_APPLICATION.LOAN_ID)));
        builder.addColumn(LOAN_APPLICATION.REQUESTED_PRINCIPAL);
        if (ProductResolver.isPayday()) {
            builder.addColumn(LOAN_APPLICATION.REQUESTED_PERIOD_COUNT).setCaption("Requested term");
        }
        builder.addColumn(LOAN_APPLICATION.OFFERED_PRINCIPAL);
        if (ProductResolver.isPayday()) {
            builder.addColumn(LOAN_APPLICATION.OFFERED_PERIOD_COUNT).setCaption("Offered term");
        }
        builder.addColumn(LOAN_APPLICATION.SUBMITTED_AT);
        builder.addColumn(LOAN_APPLICATION.SCORE);
        builder.addColumn(LOAN_APPLICATION.SCORE_BUCKET);
        builder.addComponentColumn((ValueProvider<Record, com.vaadin.ui.Component>) record -> new Label(Joiner.on(", ").join(record.get(LoanApplicationDataProvider.FIELD_ACTIVE_WORKFLOW_STEPS)))).setSortable(false).setWidth(150).setCaption("Workflow step");
        builder.addColumn(LOAN_APPLICATION.SOURCE_TYPE);
        builder.addColumn(LOAN_APPLICATION.SOURCE_NAME);
        builder.addAuditColumns(LOAN_APPLICATION);
        builder.addColumn(LOAN_APPLICATION.ID);
    }

    public PropertyLayout loanApplicationInfoSimple(LoanApplicationRecord application) {
        PropertyLayout layout = new PropertyLayout("Loan application");
        layout.addLink("Number", application.getApplicationNumber(), LoanApplicationComponents.applicationLink(application.getId()));
        layout.add("Status detail", application.getStatusDetail());
        layout.add("Close reason", application.getCloseReason());
        layout.add("Requested principal", application.getRequestedPrincipal());
        layout.add("Offered principal", application.getOfferedPrincipal());
        layout.add("Score", scoreBar(application));
        return layout;
    }

    public LoanApplicationQueries getApplicationQueries() {
        return applicationQueries;
    }

    public DSLContext getDb() {
        return db;
    }
}
