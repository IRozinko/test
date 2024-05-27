package fintech.bo.spain.alfa.applications;

import com.vaadin.spring.annotation.SpringView;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.application.AbstractLoanApplicationView;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.lending.tables.records.LoanApplicationRecord;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.spain.alfa.bo.model.RetryLoanApplicationRequest;
import org.jooq.Record2;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import java.util.Optional;

import static fintech.bo.db.jooq.workflow.Tables.WORKFLOW_;

@SpringView(name = AbstractLoanApplicationView.NAME)
public class LoanApplicationView extends AbstractLoanApplicationView {

    @Autowired
    private AlfaApiClient alfaApiClient;

    public LoanApplicationView(AlfaLoanApplicationComponents loanApplicationComponents) {
        super(loanApplicationComponents);
    }

    @Override
    protected void addCustomActions(LoanApplicationRecord application, BusinessObjectLayout layout) {
        layout.addActionMenuItem("Retry", e -> retry(application));
    }

    @Override
    protected void addCustomTabs(LoanApplicationRecord application, BusinessObjectLayout layout) {

    }

    @Override
    protected void addCustomLeftComponent(LoanApplicationRecord application, BusinessObjectLayout layout) {
        getWFInfo(application)
            .map(r-> new PropertyLayout("Workflow Info").add("Name", r.value1()).add("Version", r.value2()))
            .ifPresent(layout::addLeftComponent);
    }

    private Optional<Record2<String, Integer>> getWFInfo(LoanApplicationRecord application) {
        return Optional.ofNullable(db.select(WORKFLOW_.NAME, WORKFLOW_.VERSION)
            .from(WORKFLOW_)
            .where(WORKFLOW_.ID.eq(application.getWorkflowId()))
            .fetchOne()
        );
    }

    private void retry(LoanApplicationRecord application) {
        Dialogs.confirm("Retry application?", e -> {
            RetryLoanApplicationRequest request = new RetryLoanApplicationRequest();
            request.setApplicationId(application.getId());
            Call<Void> call = alfaApiClient.retryApplication(request);
            BackgroundOperations.callApi("Retrying application", call, t -> {
                Notifications.trayNotification("Application retry triggered");
                refresh();
            }, Notifications::errorNotification);
        });
    }
}
