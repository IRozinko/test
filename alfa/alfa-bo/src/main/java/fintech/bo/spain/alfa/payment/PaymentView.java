package fintech.bo.spain.alfa.payment;

import com.vaadin.spring.annotation.SpringView;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.payments.AbstractPaymentView;
import fintech.bo.components.payments.PaymentSummary;

@SpringView(name = AbstractPaymentView.NAME)
public class PaymentView extends AbstractPaymentView {

    @Override
    protected void addCustomActions(PaymentSummary payment, BusinessObjectLayout layout) {

    }
}
