package fintech.bo.spain.alfa.dc;

import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.client.LoanApiClient;
import fintech.bo.api.client.PaymentApiClient;
import fintech.bo.api.model.CloudFile;
import fintech.bo.api.model.client.IdsRequest;
import fintech.bo.api.model.dc.ChangeCompanyRequest;
import fintech.bo.api.model.dc.DebtEditResponse;
import fintech.bo.api.model.loan.CloudFileResponse;
import fintech.bo.components.dc.AbstractBatchDebtActionsView;
import fintech.bo.components.dc.DcQueries;
import fintech.bo.components.dc.batch.EditDebtDialog;
import fintech.bo.components.dc.batch.EditDebtDialogFactory;
import fintech.bo.components.dc.batch.EditDebtRequestBuilder;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.utils.CloudFileDownloader;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.bo.spain.alfa.dc.action.DebtStateStatusEditAction;
import fintech.bo.spain.alfa.dc.action.RepurchaseAction;
import fintech.bo.spain.alfa.dc.batch.EditDebtCompanyDialog;
import fintech.bo.spain.alfa.dc.batch.EditDebtsSummaryView;
import fintech.retrofit.RetrofitHelper;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.google.common.collect.Lists.newArrayList;
import static fintech.bo.components.background.BackgroundOperations.run;
import static fintech.bo.db.jooq.dc.Tables.DEBT;
import static fintech.bo.spain.alfa.dc.batch.BackgroundEditDebtOperation.progressiveOperation;

@Slf4j
@SpringView(name = AbstractBatchDebtActionsView.NAME)
public class BatchDebtActionsView extends AbstractBatchDebtActionsView {

    private List<Record> selected;

    private MenuBar.MenuItem sellAction;
    private MenuBar.MenuItem setStatesAction;

    private MenuBar.MenuItem revertAction;
    private MenuBar.MenuItem reassignAction;

    @Autowired
    private DcQueries dcQueries;

    @Autowired
    private AlfaApiClient alfaApiClient;

    @Autowired
    private EditDebtDialogFactory editDebtDialogFactory;

    @Autowired
    private DebtStateStatusEditAction debtStateStatusEditAction;
    @Autowired
    private FileApiClient fileApiClient;

    @Autowired
    private LoanApiClient loanApiClient;

    @Autowired
    private PaymentApiClient paymentApiClient;

    @Override
    protected void addCustomActions(GridViewLayout layout) {
        setStatesAction = layout.addActionMenuItem("Set states and statuses for debts", e -> statesAndStatusesForDebt());
        sellAction = layout.addActionMenuItem("Assign debts to collector's agency", e -> sell());
        revertAction = layout.addActionMenuItem("Recover assigned debts", e -> recoverExternal());
        reassignAction = layout.addActionMenuItem("Reassign debts to another Agents", e -> reassign());
        layout.addTopComponent(exportButton("Export debts", () ->  alfaApiClient.exportDebts(loansSelected())));
        onDebtsSelected(newArrayList());
    }

    @Override
    protected void onDebtsSelected(List<Record> selected) {
        boolean enabled = !selected.isEmpty();
        this.selected = selected;
        this.revertAction.setEnabled(enabled);
        this.sellAction.setEnabled(enabled);
        this.setStatesAction.setEnabled(enabled);
        this.reassignAction.setEnabled(enabled);
    }

    private void sell() {
        List<String> companies = dcQueries.getSettings().getCompanies().getOwningCompanies();
        EditDebtCompanyDialog editDebtCompanyDialog = EditDebtCompanyDialog.toSell(companies, company -> {
            run("Selling ...", progressiveOperation(selected, record -> {
                Call<DebtEditResponse> call = alfaApiClient.sellDebt(buildChangeCompanyRequest(record, company));
                return RetrofitHelper.syncCall(call).get();
            }), showSummary(), showException());
        });
        editDebtCompanyDialog.show();
    }

    private void statesAndStatusesForDebt() {
        debtStateStatusEditAction.edit(selected, showSummary(), showException());

    }

    private void externalize() {
        List<String> companies = dcQueries.getSettings().getCompanies().getManagingCompanies();
        EditDebtCompanyDialog editDebtCompanyDialog = EditDebtCompanyDialog.toExternalize(companies, company -> {
            run("Externalizing ...", progressiveOperation(selected, record -> {
                Call<DebtEditResponse> call = alfaApiClient.externalizeDebt(buildChangeCompanyRequest(record, company));
                return RetrofitHelper.syncCall(call).get();
            }), showSummary(), showException());
        });
        editDebtCompanyDialog.show();
    }

    private void recoverExternal() {
        EditDebtDialog dialog = editDebtDialogFactory.recoverExternal(editedDebt -> {
            run("Recovering external ...", progressiveOperation(selected, record -> {
                Call<DebtEditResponse> call = alfaApiClient.recoverExternalDebt(EditDebtRequestBuilder.build(record, editedDebt));
                return RetrofitHelper.syncCall(call).get();
            }), showSummary(), showException());
        });
        UI.getCurrent().addWindow(dialog);
    }

    private void reassign() {
        EditDebtDialog dialog = editDebtDialogFactory.reassign(editedDebt -> {
            run("Reassigning ...", progressiveOperation(selected, record -> {
                Call<DebtEditResponse> call = alfaApiClient.reassignDebt(EditDebtRequestBuilder.build(record, editedDebt));
                return RetrofitHelper.syncCall(call).get();
            }), showSummary(), showException());
        });
        UI.getCurrent().addWindow(dialog);
    }


    private ChangeCompanyRequest buildChangeCompanyRequest(Record record, String company) {
        return ChangeCompanyRequest.builder()
            .debtId(record.get(DEBT.ID))
            .company(company)
            .build();
    }

    private Consumer<List<EditDebtsSummaryView.Error>> showSummary() {
        return errors -> {
            new EditDebtsSummaryView(errors).show();
            refresh();
        };
    }

    private Consumer<Exception> showException() {
        return exception -> {
            Notifications.errorNotification(exception);
            refresh();
        };
    }

    private IdsRequest loansSelected() {
        IdsRequest loansRequest = new IdsRequest();
        List<Long> loanIdList = new ArrayList<>();
        if (selected != null && !selected.isEmpty()) {
            for (Record r : selected) {
                Long loanId = r.get(DEBT.LOAN_ID);
                loanIdList.add(loanId);
            }
        }
        loansRequest.setIds(loanIdList);
        return loansRequest;
    }

    public Component exportButton(String caption, Supplier<Call<CloudFileResponse>> call) {
        CloudFileDownloader downloader =
            new CloudFileDownloader(
                fileApiClient,
                () -> {
                    CloudFileResponse response = RetrofitHelper.syncCall(call.get())
                        .orElseThrow(IllegalStateException::new);
                    return new CloudFile(response.getFileId(), response.getOriginalFileName());
                },
                file -> Notifications.trayNotification("File downloaded: " + file.getName())
            );
        Button button = new Button(caption);
        downloader.extend(button);
        return button;
    }
}
