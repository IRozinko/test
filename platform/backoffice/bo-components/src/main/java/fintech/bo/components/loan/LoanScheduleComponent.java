package fintech.bo.components.loan;

import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.ui.*;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.CloudFile;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.utils.CloudFileResource;
import fintech.bo.db.jooq.lending.tables.records.LoanContractRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import static fintech.bo.db.jooq.lending.Tables.INSTALLMENT;
import static fintech.bo.db.jooq.lending.Tables.LOAN_CONTRACT;

public class LoanScheduleComponent extends VerticalLayout {

    private final DSLContext db;

    private FileApiClient fileApiClient;
    private final Long loanId;
    private InstallmentDataProvider installmentDataProvider;

    public LoanScheduleComponent(DSLContext db, FileApiClient fileApiClient, Long loanId) {
        this.db = db;
        this.fileApiClient = fileApiClient;
        this.loanId = loanId;
        build();
    }

    private void build() {
        ComboBox<LoanContractRecord> contractSelection = new ComboBox<>("Select contract");
        contractSelection.setItemCaptionGenerator(r ->
            String.format("%s, reason: %s",
                r.get(LOAN_CONTRACT.CONTRACT_DATE),
                r.get(LOAN_CONTRACT.SOURCE_TRANSACTION_TYPE)
            )
        );
        contractSelection.setTextInputAllowed(false);
        contractSelection.setEmptySelectionAllowed(false);
        contractSelection.setWidth(400, Unit.PIXELS);

        Result<LoanContractRecord> schedules = db.selectFrom(LOAN_CONTRACT)
            .where(LOAN_CONTRACT.LOAN_ID.eq(this.loanId))
            .orderBy(LOAN_CONTRACT.ID.desc())
            .fetch();
        contractSelection.setItems(schedules);

        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addColumn(INSTALLMENT.INSTALLMENT_NUMBER).setWidth(200);
        builder.addColumn(INSTALLMENT.STATUS).setWidth(100);
        builder.addColumn(INSTALLMENT.STATUS_DETAIL).setWidth(120);
        builder.addColumn(INSTALLMENT.DUE_DATE).setWidth(120);
        builder.addColumn(INSTALLMENT.TOTAL_INVOICED).setWidth(100);
        builder.addColumn(INSTALLMENT.TOTAL_PAID).setWidth(100);
        builder.addColumn(INSTALLMENT.TOTAL_DUE).setWidth(100);
        builder.addComponentColumn(this::invoiceFileColumn).setCaption("Invoice file").setWidth(250);
        builder.addColumn(INSTALLMENT.PERIOD_FROM).setWidth(120);
        builder.addColumn(INSTALLMENT.PERIOD_TO).setWidth(120);
        builder.addColumn(INSTALLMENT.GENERATE_INVOICE_ON_DATE);
        builder.addColumn(INSTALLMENT.INVOICE_FILE_GENERATED_AT);
        builder.addColumn(INSTALLMENT.INVOICE_FILE_SENT_AT);
        builder.addColumn(INSTALLMENT.ID);
        builder.addAuditColumns(INSTALLMENT);
        builder.sortAsc(INSTALLMENT.PERIOD_FROM);

        installmentDataProvider = new InstallmentDataProvider(db);
        Grid<Record> installmentGrid = builder.build(installmentDataProvider);

        addComponent(contractSelection);
        addComponent(new Label("Installments"));
        addComponentsAndExpand(installmentGrid);

        contractSelection.addValueChangeListener(e -> scheduleSelected(e.getValue()));
        if (!schedules.isEmpty()) {
            contractSelection.setValue(schedules.get(0));
        }
    }

    private com.vaadin.ui.Component invoiceFileColumn(Record record) {
        Long fileId = record.get(INSTALLMENT.INVOICE_FILE_ID);
        if (fileId == null) {
            return new Label("-");
        }

        CloudFile cloudFile = new CloudFile(record.get(INSTALLMENT.INVOICE_FILE_ID), record.get(INSTALLMENT.INVOICE_FILE_NAME));
        Link link = new Link();
        link.setCaption(record.get((INSTALLMENT.INVOICE_FILE_NAME)));
        CloudFileResource resource = new CloudFileResource(cloudFile, fileApiClient, f -> {});
        BrowserWindowOpener opener = new BrowserWindowOpener(resource);
        opener.extend(link);

        return link;
    }

    private void scheduleSelected(LoanContractRecord selected) {
        installmentDataProvider.setContractId(selected == null ? null : selected.getId());
        installmentDataProvider.refreshAll();
    }
}
