package fintech.bo.spain.alfa.dc.action;

import com.vaadin.ui.UI;
import fintech.bo.api.client.DcApiClient;
import fintech.bo.api.model.dc.DebtEditResponse;
import fintech.bo.api.model.dc.SaveDebtStatusRequest;
import fintech.bo.components.dc.DebtStatusStateDialog;
import fintech.bo.components.dc.batch.EditDebtDialogFactory;
import fintech.bo.spain.alfa.dc.batch.EditDebtsSummaryView;
import fintech.retrofit.RetrofitHelper;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Call;

import java.util.List;
import java.util.function.Consumer;

import static fintech.bo.components.background.BackgroundOperations.run;
import static fintech.bo.db.jooq.dc.Tables.DEBT;
import static fintech.bo.spain.alfa.dc.batch.BackgroundEditDebtOperation.progressiveOperation;

@Component
public class DebtStateStatusEditAction {

    @Autowired
    private DcApiClient dcApiClient;

    @Autowired
    private EditDebtDialogFactory editDebtDialogFactory;

    public void edit(List<Record> debts, Consumer<List<EditDebtsSummaryView.Error>> onSuccess, Consumer<Exception> onError) {
        DebtStatusStateDialog dialog = editDebtDialogFactory.editDebtStateAndStatus(editedDebt -> {
            run("Updating state and status for debt ...", progressiveOperation(debts, record -> {
                SaveDebtStatusRequest request = new SaveDebtStatusRequest();
                request.setDebtId(record.get(DEBT.ID));
                request.setState(editedDebt.getState());
                request.setStatus(editedDebt.getStatus());
                Call<DebtEditResponse> call = dcApiClient.saveDebtStatus(request);
                return RetrofitHelper.syncCall(call).get();
            }), onSuccess, onError);
        });
        UI.getCurrent().addWindow(dialog);
    }
}
