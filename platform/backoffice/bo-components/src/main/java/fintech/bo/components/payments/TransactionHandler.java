package fintech.bo.components.payments;

import com.vaadin.ui.Component;
import fintech.bo.db.jooq.payment.tables.records.PaymentRecord;
import retrofit2.Call;

import java.util.Optional;

public interface TransactionHandler extends Component {

    void init(PaymentRecord record, AddTransactionComponent parent);

    Optional<Call<?>> saveCall();
}
