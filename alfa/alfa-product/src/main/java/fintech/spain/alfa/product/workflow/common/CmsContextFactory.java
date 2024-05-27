package fintech.spain.alfa.product.workflow.common;

import fintech.spain.platform.web.SpecialLinkType;
import fintech.spain.alfa.product.cms.ApplicationModel;
import fintech.spain.alfa.product.cms.LoanModel;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import fintech.workflow.Workflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CmsContextFactory {

    @Autowired
    private AlfaCmsModels cmsModels;

    public Map<String, Object> getContext(Workflow workflow) {
        Map<String, Object> context = new HashMap<>();

        if (workflow.getApplicationId() != null) {
            ApplicationModel model = cmsModels.application(workflow.getApplicationId());
            context.put(AlfaCmsModels.SCOPE_APPLICATION, model);
            context.put(AlfaCmsModels.SCOPE_CALCULATION_STRATEGY, cmsModels.calculationStrategyByApplication(workflow.getApplicationId()));
        }
        if (workflow.getLoanId() != null) {
            LoanModel model = cmsModels.loan(workflow.getLoanId());
            context.put(AlfaCmsModels.SCOPE_LOAN, model);
            context.put(AlfaCmsModels.SCOPE_CALCULATION_STRATEGY, cmsModels.calculationStrategyByLoan(workflow.getLoanId()));
        }

        // TODO do not use a special link, use auto login
            Map<String, Object> model = cmsModels.specialLink(workflow.getApplicationId(), SpecialLinkType.LOC_SPECIAL_OFFER);
            context.put(AlfaCmsModels.SCOPE_SPECIAL_LINK, model.get(AlfaCmsModels.SCOPE_SPECIAL_LINK));

        return context;
    }

}
