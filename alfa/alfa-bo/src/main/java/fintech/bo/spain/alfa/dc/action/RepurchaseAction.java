package fintech.bo.spain.alfa.dc.action;

import com.vaadin.ui.UI;
import fintech.retrofit.RetrofitHelper;
import fintech.bo.api.model.dc.DebtEditResponse;
import fintech.bo.components.dc.batch.EditDebtDialog;
import fintech.bo.components.dc.batch.EditDebtDialogFactory;
import fintech.bo.components.dc.batch.EditDebtRequestBuilder;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.bo.spain.alfa.dc.batch.EditDebtsSummaryView;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Call;

import java.util.List;
import java.util.function.Consumer;

import static fintech.bo.components.background.BackgroundOperations.run;
import static fintech.bo.spain.alfa.dc.batch.BackgroundEditDebtOperation.progressiveOperation;

@Component
public class RepurchaseAction {

    @Autowired
    private AlfaApiClient alfaApiClient;

    @Autowired
    private EditDebtDialogFactory editDebtDialogFactory;

    public void repurchase(List<Record> debts, Consumer<List<EditDebtsSummaryView.Error>> onSuccess, Consumer<Exception> onError) {
        EditDebtDialog dialog = editDebtDialogFactory.repurchase(editedDebt -> {
            run("Repurchasing ...", progressiveOperation(debts, record -> {
                Call<DebtEditResponse> call = alfaApiClient.repurchaseDebt(EditDebtRequestBuilder.build(record, editedDebt));
                return RetrofitHelper.syncCall(call).get();
            }), onSuccess, onError);
        });
        UI.getCurrent().addWindow(dialog);
    }

}
