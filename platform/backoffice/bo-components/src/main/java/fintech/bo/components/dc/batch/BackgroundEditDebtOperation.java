package fintech.bo.components.dc.batch;

import com.google.common.collect.Lists;
import fintech.bo.api.model.dc.DebtEditResponse;
import fintech.bo.components.background.BackgroundOperation;
import org.jooq.Record;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.google.common.collect.ImmutableList.copyOf;
import static fintech.bo.db.jooq.dc.Tables.DEBT;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class BackgroundEditDebtOperation {

    public static BackgroundOperation<List<EditDebtsSummaryView.Error>> progressiveOperation(List<Record> records, Function<Record, DebtEditResponse> onNextRecord) {
        return feedback -> {
            List<EditDebtsSummaryView.Error> errors = Lists.newArrayList();
            for (int i = 0; i < records.size(); i++) {
                feedback.update(format("%s / %s", i, records.size()), (i + 1) / (float) records.size());
                DebtEditResponse response = onNextRecord.apply(records.get(i));
                if (isNotBlank(response.getErrorMessage())) {
                    errors.add(EditDebtsSummaryView.Error.builder()
                        .debtId(records.get(i).get(DEBT.ID))
                        .errorMessage(response.getErrorMessage())
                        .build());
                }
            }
            return copyOf(errors);
        };
    }

    public static BackgroundOperation<Void> voidProgressiveOperation(List<Record> records, Consumer<Record> onNextRecord) {
        return feedback -> {
            for (int i = 0; i < records.size(); i++) {
                feedback.update(format("%s / %s", i, records.size()), (i + 1) / (float) records.size());
                onNextRecord.accept(records.get(i));
            }
            return null;
        };
    }

}
