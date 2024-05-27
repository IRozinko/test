package fintech.spain.alfa.product.cms;

import com.google.common.collect.Maps;
import fintech.cms.CmsContextBuilder;
import fintech.dc.impl.DcServiceBean;
import fintech.lending.core.application.LoanApplicationQuery;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Primary
@Component
public class AlfaCmsContextBuilder implements CmsContextBuilder {

    @Autowired
    private AlfaCmsModels cmsModels;

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private DcServiceBean dcService;

    @Override
    public String companyLocale() {
        return cmsModels.company().getDefaultLocale();
    }

    @Override
    public Map<String, Object> basicContext(Long clientId, Long debtId) {

        Map<String, Object> context = new HashMap<>();
        if (clientId != null) {
            loanApplicationService.findLatest(LoanApplicationQuery.byClientId(clientId))
                .ifPresent(application -> context.putAll(cmsModels.applicationContext(application.getId())));

            loanService.findLastLoan(LoanQuery.nonVoidedLoans(clientId)).ifPresent(loan -> {
                context.putAll(cmsModels.loanContext(loan.getId()));
                dcService.findByLoanId(loan.getId())
                    .ifPresent(debt -> context.putAll(cmsModels.debtContext(debt.getId())));
            });

            context.putAll(cmsModels.clientContext(clientId));
        }

        if (debtId != null) {
            context.putAll(cmsModels.debtContext(debtId));
        }

        return context;
    }

    @Override
    public Map<String, Object> anonymousNotificationContext(Map<String, Object> context) {
        return mergeContexts(context, Maps.immutableEntry(AlfaCmsModels.SCOPE_COMPANY, cmsModels.company()));
    }

    @Override
    public Map<String, Object> basicContext(Long clientId, Map<String, Object> context) {
        return mergeContexts(context,
            Maps.immutableEntry(AlfaCmsModels.SCOPE_COMPANY, cmsModels.company()),
            Maps.immutableEntry(AlfaCmsModels.SCOPE_CLIENT, cmsModels.client(clientId)));
    }

    protected Map<String, Object> mergeContexts(Map<String, Object> context, Map.Entry<String, Object>... entry) {
        Map<String, Object> cmsContext = Stream.of(entry)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        cmsContext.putAll(context);
        return cmsContext;
    }

}
