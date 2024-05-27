package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.payments.DisbursementService;
import fintech.payments.model.Disbursement;
import fintech.payments.model.DisbursementStatusDetail;
import fintech.spain.alfa.product.workflow.common.Attributes;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.AutoCompletePrecondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import java.util.Optional;



@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class DisbursementExported implements AutoCompletePrecondition {

    @Autowired
    private DisbursementService disbursementService;

    @Override
    public boolean isTrueFor(ActivityContext context) {
        Optional<DisbursementStatusDetail> statusDetail = context.getAttribute(Attributes.DISBURSEMENT_ID)
            .map(Long::valueOf)
            .map(disbursementService::getDisbursement)
            .map(Disbursement::getStatusDetail);

        return statusDetail.isPresent() && statusDetail.get() == DisbursementStatusDetail.EXPORTED;
    }
}
